import sys
import wikipedia
import warnings
import io

# 1. Ignorăm avertismentele de parser pentru a nu polua output-ul către JADE
warnings.filterwarnings("ignore", category=UserWarning, module='wikipedia')

# 2. Forțăm output-ul în UTF-8 pentru caracterele românești
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

wikipedia.set_lang("ro")

def get_definition():
    if len(sys.argv) < 2:
        return
    
    query = " ".join(sys.argv[1:])
    try:
        # Preluăm prima propoziție din rezumat
        summary = wikipedia.summary(query, sentences=1)
        # Printează strict formatul așteptat de Java
        print(f"OK|{summary}")
    except wikipedia.exceptions.DisambiguationError as e:
        options = ", ".join(e.options[:3])
        print(f"OK|Termen prea general. Incearca: {options}")
    except wikipedia.exceptions.PageError:
        print("OK|Nu am gasit informatii pe Wikipedia despre acest subiect.")
    except Exception as e:
        print(f"OK|Eroare neasteptata: {str(e)}")

if __name__ == "__main__":
    get_definition()