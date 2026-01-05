import sys
import psutil
import io

# Setăm output-ul pe UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def get_system_status():
    # Preluăm consumul de CPU (interval de 0.1s pentru acuratețe)
    cpu_usage = psutil.cpu_percent(interval=0.1)
    
    # Preluăm datele despre RAM
    memory = psutil.virtual_memory()
    ram_usage = memory.percent
    ram_free = round(memory.available / (1024**3), 2) # Conversie în GB
    
    print(f"OK|CPU: {cpu_usage}% | RAM: {ram_usage}% (Liber: {ram_free} GB)")

if __name__ == "__main__":
    get_system_status()