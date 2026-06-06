import urllib.request
import urllib.error
import json
import os
from dotenv import load_dotenv

load_dotenv()
api_key = os.getenv("GOOGLE_API_KEY")

url = f'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={api_key}'
data = json.dumps({'contents': [{'parts': [{'text': 'Hello'}]}]}).encode('utf-8')
req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'}, method='POST')

try:
    with urllib.request.urlopen(req) as f:
        print(f.read().decode('utf-8'))
except urllib.error.HTTPError as e:
    print('HTTPError:', e.code)
    print('Reason:', e.reason)
    print('Body:', e.read().decode('utf-8'))
except Exception as e:
    print('Exception:', e)
