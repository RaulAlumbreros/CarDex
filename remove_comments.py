import os
import re

def remove_comments(text):
    # Regex para comentarios de bloque /* ... */
    text = re.sub(r'/\*.*?\*/', '', text, flags=re.DOTALL)
    # Regex para comentarios de línea // ...
    # Aseguramos no borrar // dentro de strings de URL por ejemplo
    # Pero lo simplificamos para el código Kotlin
    text = re.sub(r'(?<!:)\/\/.*', '', text)
    return text

def process_directory(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".kt") or file.endswith(".xml"):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                new_content = remove_comments(content)
                
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)

if __name__ == "__main__":
    src_path = r"c:\Users\raul.alumbreros.jann\AndroidStudioProjects\CarDex\app\src"
    process_directory(src_path)
    print("Comments removed successfully.")
