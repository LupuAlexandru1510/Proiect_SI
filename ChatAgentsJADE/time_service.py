import sys
from datetime import datetime
import pytz
import io

# Setăm output-ul pe UTF-8 pentru a evita erorile de caractere
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def get_time():
    # Dacă nu primește parametru, returnează ora locală
    if len(sys.argv) < 2 or sys.argv[1].strip() == "":
        now = datetime.now().strftime("%H:%M:%S")
        print(f"OK|Ora locala (Sistem): {now}")
        return

    city_query = sys.argv[1].lower().strip()
    
    # Mapare simplă pentru orașe populare (Continent/Oras)
    zones = {
        "london": "Europe/London",
        "tokyo": "Asia/Tokyo",
        "new york": "America/New_York",
        "paris": "Europe/Paris",
        "bucuresti": "Europe/Bucharest",
        "dubai": "Asia/Dubai",
        "sydney": "Australia/Sydney"
    }

    try:
        if city_query in zones:
            tz_name = zones[city_query]
        else:
            # Încercăm să formatăm automat dacă nu e în listă (ex: Berlin -> Europe/Berlin)
            tz_name = f"Europe/{city_query.capitalize()}"
            
        tz = pytz.timezone(tz_name)
        now = datetime.now(tz).strftime("%H:%M:%S")
        print(f"OK|Ora in {city_query.capitalize()}: {now}")
    except Exception:
        print(f"OK|Nu am gasit fusul orar pentru '{city_query}'. Incearca: London, Tokyo, New York.")

if __name__ == "__main__":
    get_time()