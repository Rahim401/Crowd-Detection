import sqlite3
from os import makedirs, path
from datetime import timedelta
from CrowdBackend import ServerDir
from CrowdBackend.Utils import avg, str2Time, time2Str


class DataManager:
    databaseDir = f"{ServerDir}/database"
    databasePath = f"{databaseDir}/CrowdDatabase.db"
    def __init__(self, dbName=databasePath):
        self.dbName = dbName
        self.connection = None
        self.cursor = None
        self.connectToDatabase()
        self.createTables()

    def connectToDatabase(self):
        try:
            dbDir = path.dirname(self.dbName)
            if not path.exists(dbDir): makedirs(dbDir)
            self.connection = sqlite3.connect(self.dbName, check_same_thread=False)
            self.connection.execute("PRAGMA foreign_keys = ON;")
            self.cursor = self.connection.cursor()
        except sqlite3.Error as e:
            print(f"Error connecting to database: {e}")

    def createTables(self):
        try:
            # Create Location table
            self.cursor.execute('''
                CREATE TABLE IF NOT EXISTS Location (
                    Name TEXT PRIMARY KEY,
                    Address TEXT,
                    CreatedBy TEXT
                )
            ''')

            # Create Record table
            self.cursor.execute('''
                CREATE TABLE IF NOT EXISTS Record (
                    Location TEXT,
                    AtTime DATETIME,
                    FromEmail TEXT,
                    Message TEXT,
                    PhotoPath TEXT,
                    CrowdCount INTEGER,
                    PRIMARY KEY (Location, AtTime),
                    FOREIGN KEY (Location) REFERENCES Location(Name)
                )
            ''')
            self.connection.commit()
        except sqlite3.Error as e:
            print(f"Error creating tables: {e}")

    def insertLocation(self, name, address, by="admin@crowd.com"):
        try:
            self.cursor.execute('''
                INSERT INTO Location (Name, Address, CreatedBy) 
                VALUES (?, ?, ?)
            ''', (name, address, by))
            self.connection.commit()
        except sqlite3.Error as e:
            print(f"Error inserting into Location table: {e}")

    def insertRecord(self, atLocation, atTime, fromEmail, message, photoPath, crowdAt):
        try:
            self.cursor.execute('''
                INSERT INTO Record (Location, AtTime, FromEmail, Message, PhotoPath, CrowdCount) 
                VALUES (?, ?, ?, ?, ?, ?)
            ''', (atLocation, atTime, fromEmail, message, photoPath, crowdAt))
            self.connection.commit()
        except sqlite3.Error as e:
            print(f"Error inserting into Record table: {e}")

    def getAllLocations(self):
        try:
            self.cursor.execute('SELECT * FROM Location')
            return self.cursor.fetchall()
        except sqlite3.Error as e:
            print(f"Error fetching locations: {e}")
            return []

    def isLocationIn(self, location):
        if location is not None:
            try:
                self.cursor.execute('SELECT * FROM Location WHERE Name = ?', (location, ))
                return any(self.cursor.fetchall())
            except sqlite3.Error as e:
                print(f"Error fetching locations: {e}")
        return False

    def getRecordsByLocation(self, location):
        if location is not None:
            try:
                self.cursor.execute('SELECT * FROM Record WHERE Location = ?', (location,))
                return self.cursor.fetchall()
            except sqlite3.Error as e:
                print(f"Error fetching records: {e}")
        return []

    def getAvgCrowdOf(self, location):
        if location is not None:
            try:
                self.cursor.execute(
                    '''
                        SELECT AVG(CrowdCount)
                        FROM Record
                        WHERE Location = ? AND CrowdCount >= 1
                    ''', (location,)
                )
                result = self.cursor.fetchone()
                if result and len(result) == 1 and result[0]:
                    return result[0]
            except sqlite3.Error as e: print(f"Error querying records: {e}")
        return 0

    def getCrowdAt(self, location, time):
        if isinstance(time, str): time = str2Time(time)
        # Error No enough data
        if location is None or time is None: return -1
        # Error Invalid Data
        if not self.isLocationIn(location): return -2
        timeWindowStart = time2Str(time - timedelta(minutes=60))
        timeWindowEnd = time2Str(time)
        queryDetails = {
            "WHERE Location = ? AND AtTime BETWEEN ? AND ?": (location, timeWindowStart, timeWindowEnd),
            "WHERE Location = ? AND strftime('%w', AtTime) = ? AND strftime('%H', AtTime) = ?":
                (location, str(time.weekday()+1), f"{time.hour:02d}"),
            "WHERE Location = ? AND strftime('%H', AtTime) = ?": (location, f"{time.hour:02d}"),
        }

        try:
            # Query for records within 30 minutes before and after the given time
            for idx, (condition, arg) in enumerate(queryDetails.items()):
                self.cursor.execute(
                    f'''
                        SELECT COUNT(CrowdCount), MIN(CrowdCount), MAX(CrowdCount), AVG(CrowdCount)
                        FROM Record {condition}
                    ''',  arg
                )
                result = self.cursor.fetchone()
                if result and any(result):
                    # Proper Result
                    return idx+1, *result
            # Error No Enough Records to Analyze
            return (0,)
        except sqlite3.Error as e:
            print(f"Error querying records: {e}")
            # Error RuntimeError
            return -10, e

    def getAdvCrowdDetailsAt(self, location, time):
        if isinstance(time, str): time = str2Time(time)
        # Error No enough data
        if location is None or time is None: return (-1, )
        # Error Invalid Data
        if not self.isLocationIn(location): return (-2, )
        noOfNextHrToCheck = 4; resCode = 0;
        avgCrowdOn4Hrs = 0; lowCrowdAt = -1
        crownOnN4Hrs = []

        avgCrowd = self.getAvgCrowdOf(location)
        if avgCrowd > 0:
            crownOnN4Hrs = [
                self.getCrowdAt(location, time+timedelta(hours=hr))
                for hr in range(noOfNextHrToCheck)
            ]
            avgCrowdOn4Hrs = avg([data[3] for data in crownOnN4Hrs if data[0] > 0])

            if avgCrowdOn4Hrs > 0:
                resCode = 1
                for hr, crdAtHr in enumerate(crownOnN4Hrs):
                    if crdAtHr[0] == 0: continue

                    if lowCrowdAt < 0: lowCrowdAt = hr
                    elif crdAtHr[3] < crownOnN4Hrs[lowCrowdAt][3]:
                        lowCrowdAt = hr

                if lowCrowdAt >= 0:
                    resCode = 2
        return resCode, avgCrowd, avgCrowdOn4Hrs, lowCrowdAt, crownOnN4Hrs

    # If photoNeeded only chooses Record with Photo,
    # Even if Record near time without photo exists
    def getRecordNear(self, location, time, recordWith="PhotoOnly"):
        if isinstance(time, str): time = str2Time(time)
        # Error No enough data
        if location is None or time is None: return (-1, )
        # Error Invalid Data
        if not self.isLocationIn(location): return (-2, )
        strTime = time2Str(time)
        queryDetails = {
            "WHERE Location = ?"
            "ORDER BY ABS(strftime('%s', AtTime) - strftime('%s', ?))": (location, strTime),
            "WHERE Location = ? AND strftime('%w', AtTime) = ? AND strftime('%H', AtTime) = ?":
                (location, str(time.weekday()+1), f"{time.hour:02d}"),
            "WHERE Location = ? AND strftime('%H', AtTime) = ?": (location, f"{time.hour:02d}"),
            "WHERE Location = ?": (location, ),
        }

        try:
            recordCond = ""
            if recordWith == "PhotoOnly": recordCond = "AND PhotoPath != ''"
            elif recordWith == "NoPhotoOnly": recordCond = "AND PhotoPath = ''"
            for idx, (condition, arg) in enumerate(queryDetails.items()):
                self.cursor.execute(
                    # """select atTime, crowdCount, photoPath from Record where photoPath=''"""
                    f'''
                        SELECT AtTime, PhotoPath, CrowdCount
                        FROM Record {condition} 
                        AND PhotoPath = ''
                    ''',  arg
                )
                result = self.cursor.fetchone()
                if result and any(result):
                    # Proper Result
                    return idx+1, *result
            # Error No Enough Records to Analyze
            return (0, )
        except sqlite3.Error as e:
            print(f"Error querying records: {e}")
            # Error RuntimeError
            return -10, e

    def closeConnection(self):
        if self.connection:
            self.connection.close()

# Example Usage
if __name__ == "__main__":
    dataManager = DataManager()
    print(dataManager.isLocationIn("MG Road2"))
    # for hr in range(200):
    #     print(dataManager.getAdvCrowdDetailsAt("Indiranagar", datetime.now() - timedelta(hours=hr)))
    # Close connection when done
    dataManager.closeConnection()
