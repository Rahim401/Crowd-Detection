import argparse
from CrowdApi import CrowdApi
from datetime import datetime as dt

# Initialize the API instance
api = CrowdApi()

def list_locations(args):
    """Handle lsLoc subcommand to list all locations."""
    print("\nExecuting: List Locations")
    resCode, resData = api.getLocationRes()
    if resCode == 200:
        locations = resData.get("allLocations", [])
        print("Available Locations:")
        for loc in locations:
            print(f"- {loc}")
    else:
        print(f"Failed to fetch locations. Status: {resCode}, Error: {resData}")

def make_location(args):
    """Handle mkLoc subcommand to create a new location."""
    print(f"\nExecuting: Create Location (Place: {args.place}, Address: {args.address})")
    resCode, resData = api.createLocationRes(args.place, args.address)
    print(f"Response: {resCode}, {resData}")

def post_crowd(args):
    """Handle post subcommand to post crowd data."""
    print("\nExecuting: Post Crowd Data")
    resCode, resData = api.postCrowdAtRes(
        atLocation=args.location,
        atTime=dt.strptime(args.time, "%Y-%m-%d %H:%M:%S"),
        fromMail=args.email,
        message=args.message,
        photoPath=args.photo,
        crowdAt=args.crowd
    )
    print(f"Response: {resCode}, {resData}")

def get_estimation(args):
    """Handle getEst subcommand to fetch crowd estimation."""
    print(f"\nExecuting: Get Estimation (Location: {args.location}, Time: {args.time})")
    resCode, resData = api.getEstimationRes(
        atLocation=args.location,
        atTime=dt.strptime(args.time, "%Y-%m-%d %H:%M:%S"),
        fromMail=args.email
    )
    print(f"Response: {resCode}, {resData}")

def get_photo(args):
    """Handle getPhoto subcommand to fetch photos near the specified location and time."""
    print(f"\nExecuting: Get Photo Near (Location: {args.location}, Time: {args.time})")
    resCode, resData = api.getPhotoNearRes(
        atLocation=args.location,
        atTime=dt.strptime(args.time, "%Y-%m-%d %H:%M:%S"),
        recordWith=args.record_with
    )
    print(f"Response: {resCode}, {resData}")

# Define main CLI parser
parser = argparse.ArgumentParser(description="Crowd API CLI Tool")
subparsers = parser.add_subparsers(title="Subcommands", dest="command", required=True)

# Subcommand: lsLoc
parser_lsLoc = subparsers.add_parser("lsLoc", help="List all locations")
parser_lsLoc.set_defaults(func=list_locations)

# Subcommand: mkLoc
parser_mkLoc = subparsers.add_parser("mkLoc", help="Create a new location")
parser_mkLoc.add_argument("place", type=str, help="Name of the place")
parser_mkLoc.add_argument("address", type=str, help="Address of the location")
parser_mkLoc.set_defaults(func=make_location)

# Subcommand: post
parser_post = subparsers.add_parser("post", help="Post crowd data")
parser_post.add_argument("location", type=str, help="Location name")
parser_post.add_argument("time", type=str, help="Time (format: YYYY-MM-DD HH:MM:SS)")
parser_post.add_argument("email", type=str, help="Email of the user")
parser_post.add_argument("message", type=str, help="Message about the crowd")
parser_post.add_argument("--photo", type=str, help="Path to the photo file", default=None)
parser_post.add_argument("--crowd", type=int, help="Crowd density", default=-1)
parser_post.set_defaults(func=post_crowd)

# Subcommand: getEst
parser_getEst = subparsers.add_parser("getEst", help="Get crowd estimation")
parser_getEst.add_argument("location", type=str, help="Location name")
parser_getEst.add_argument("time", type=str, help="Time (format: YYYY-MM-DD HH:MM:SS)")
parser_getEst.add_argument("--email", type=str, help="Email of the user", default="noUser@crowd.com")
parser_getEst.set_defaults(func=get_estimation)

# Subcommand: getPhoto
parser_getPhoto = subparsers.add_parser("getPhoto", help="Get photos near a location and time")
parser_getPhoto.add_argument("location", type=str, help="Location name")
parser_getPhoto.add_argument("time", type=str, help="Time (format: YYYY-MM-DD HH:MM:SS)")
parser_getPhoto.add_argument("--record_with", type=str, choices=["PhotoOnly", "NoPhotoOnly"], default="PhotoOnly",
                             help="Filter to include only photos or exclude photos")
parser_getPhoto.set_defaults(func=get_photo)

# Parse and execute the appropriate subcommand
if __name__ == "__main__":
    args = parser.parse_args()
    args.func(args)
