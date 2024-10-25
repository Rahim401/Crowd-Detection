import sqlite3, os
from Utils import avg
from datetime import datetime, timedelta


class DataManager:
    databasePath = "database/CrowdDatabase.db"
    def __init__(self, dbName=databasePath):
        self.dbName = dbName
        self.connection = None
        self.cursor = None
        self.connectToDatabase()
        self.createTables()

    def connectToDatabase(self):
        try:
            dbDir = os.path.dirname(self.dbName)
            if not os.path.exists(dbDir): os.makedirs(dbDir)
            self.connection = sqlite3.connect(self.dbName, check_same_thread=False)
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

    def insertRecord(self, location, atTime, fromEmail, message, crowdCount):
        try:
            self.cursor.execute('''
                INSERT INTO Record (Location, AtTime, FromEmail, Message, CrowdCount) 
                VALUES (?, ?, ?, ?, ?)
            ''', (location, atTime, fromEmail, message, crowdCount))
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
        try:
            self.cursor.execute('SELECT * FROM Location WHERE Name = ?', (location, ))
            return any(self.cursor.fetchall())
        except sqlite3.Error as e:
            print(f"Error fetching locations: {e}")
            return []

    def getRecordsByLocation(self, location):
        try:
            self.cursor.execute('SELECT * FROM Record WHERE Location = ?', (location,))
            return self.cursor.fetchall()
        except sqlite3.Error as e:
            print(f"Error fetching records: {e}")
            return []

    def getAvgCrowdOf(self, location):
        try:
            self.cursor.execute(
                '''
                    SELECT AVG(CrowdCount)
                    FROM Record
                    WHERE Location = ? AND CrowdCount >= 1
                ''', (location,)
            )
            result = self.cursor.fetchone()
            if result and len(result) == 1:
                return result[0]
        except sqlite3.Error as e: print(f"Error querying records: {e}")
        return 0

    def getCrowdAt(self, location, time: datetime):
        timeWindowStart = (time - timedelta(minutes=60)).strftime("%Y-%m-%d %H:%M:%S")
        timeWindowEnd = time.strftime("%Y-%m-%d %H:%M:%S")
        queryDetails = {
            "WHERE Location = ? AND AtTime BETWEEN ? AND ?": (location, timeWindowStart, timeWindowEnd),
            "WHERE Location = ? AND strftime('%w', AtTime) = ? AND strftime('%H', AtTime) = ?":
                (location, str(time.weekday()), f"{time.hour:02d}"),
            "WHERE Location = ? AND strftime('%H', AtTime) = ?": (location, f"{time.hour:02d}"),
        }

        try:
            # Query for records within 30 minutes before and after the given time
            for idx, (condition, arg) in enumerate(queryDetails.items()):
                self.cursor.execute(
                    '''
                        SELECT COUNT(CrowdCount), MIN(CrowdCount), MAX(CrowdCount), AVG(CrowdCount)
                        FROM Record
                    ''' + condition,  arg
                )
                result = self.cursor.fetchone()
                if result and any(result): return idx+1, *result
        except sqlite3.Error as e: print(f"Error querying records: {e}")
        return (0, )


    def getAdvCrowdDetailsAt(self, location, time: datetime):
        noOfNextHrToCheck = 4
        resCode = -1;
        avgCrowdOn4Hrs = 0
        lowCrowdAt = -1
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
