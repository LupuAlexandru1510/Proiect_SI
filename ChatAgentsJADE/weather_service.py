import sys
import requests
import io

# Forțăm UTF-8 pentru caracterele speciale (grade Celsius)
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def get_weather():
    if len(sys.argv) < 2:
        print("OK|Introdu un oras (ex: Bucuresti)")
        return
    
    city = sys.argv[1]
    try:
        # Folosim formatul j1 pentru JSON de la wttr.in
        url = f"https://wttr.in/{city}?format=j1"
        response = requests.get(url, timeout=10)
        data = response.json()
        
        current = data['current_condition'][0]
        temp = current['temp_C']
        desc = current['lang_ro'][0]['value'] if 'lang_ro' in current else current['weatherDesc'][0]['value']
        umiditate = current['humidity']
        
        print(f"OK|Vremea in {city}: {temp}°C, {desc}, Umiditate: {umiditate}%")
    except Exception as e:
        print(f"OK|Nu am putut gasi vremea pentru {city}. Verifica numele orasului.")

if __name__ == "__main__":
    get_weather()