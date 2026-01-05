import sys
import json
from pydantic import BaseModel, Field, field_validator
from datetime import datetime

# Definim schema de date conform Pydantic V2
class AgentTask(BaseModel):
    sender: str
    command: str
    payload: str
    timestamp: datetime = Field(default_factory=datetime.now)

    @field_validator('command')
    @classmethod
    def validate_command(cls, v):
        allowed = ['UPPER', 'REVERSE', 'TIME']
        if v.upper() not in allowed:
            raise ValueError(f"Comanda {v} nu este suportata")
        return v.upper()

def process():
    if len(sys.argv) < 2: return
    try:
        # Primim datele formatate: sender|command|payload
        raw_input = sys.argv[1]
        parts = raw_input.split("|")
        
        # Validare si procesare
        task = AgentTask(sender=parts[0], command=parts[1], payload=parts[2])
        
        result = ""
        if task.command == "UPPER": result = task.payload.upper()
        elif task.command == "REVERSE": result = task.payload[::-1]
        elif task.command == "TIME": result = task.timestamp.strftime("%H:%M:%S")
        
        print(f"OK|{result}")
    except Exception as e:
        print(f"ERROR|{str(e)}")

if __name__ == "__main__":
    process()