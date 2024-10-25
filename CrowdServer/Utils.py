from flask import make_response, jsonify

def avg(lst: list[int]): return sum(lst) / len(lst)

def response(status: int = 200, *args, **kwargs):
    return make_response(jsonify(*args, **kwargs), status)