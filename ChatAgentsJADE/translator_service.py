import sys
import json
from deep_translator import GoogleTranslator

def translate():
    if len(sys.argv) < 2:
        print("ERROR|Lipseste textul")
        return
        
    try:
        # Preluam textul (care poate contine spatii)
        text_to_translate = sys.argv[1]
        
        # Traducere automata din Romana in Engleza
        translated = GoogleTranslator(source='ro', target='en').translate(text_to_translate)
        
        # Trimitem rezultatul inapoi catre Java
        if translated:
            print(f"OK|{translated}")
        else:
            print("ERROR|Traducere goala")
            
    except Exception as e:
        print(f"ERROR|{str(e)}")

if __name__ == "__main__":
    translate()