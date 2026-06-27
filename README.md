# GoLite Interpreter - Fase 1
## Organización de Lenguajes y Compiladores 1
### Universidad San Carlos de Guatemala - FIUSAC

**Estudiante:** Dalio Miranda  
**Carnet:** 202100116  
**Repositorio:** EJ26_OLC1_P1_202100116

## Descripción
Intérprete para el lenguaje GoLite implementado en Java usando JFlex y CUP.

## Requisitos
- Java JDK 21
- Apache Maven 3.9+

## Cómo ejecutar
```cmd
mvn compile exec:java
```

## Tecnologías
- Java 21
- JFlex 1.9.1
- CUP 11b
- Java Swing (GUI)
# GoLite IDE - Fase 2

Proyecto desarrollado para el curso **Organización de Lenguajes y Compiladores 1**.

**Estudiante:** Dalio Antonio Miranda Guerra
**Carnet:** 202100116

---

## Descripción

GoLite IDE es una herramienta desarrollada en Java que permite escribir, analizar y ejecutar código del lenguaje GoLite.
El proyecto incluye análisis léxico, análisis sintáctico, generación de AST, interpretación del código, manejo de errores y reportes visuales.

La interfaz gráfica permite crear, abrir, guardar y ejecutar archivos con extensión `.glt`.

---

## Tecnologías utilizadas

* Java
* Maven
* JFlex
* CUP
* Swing
* Graphviz

---

## Requisitos

Para ejecutar correctamente el proyecto se necesita tener instalado:

* Java JDK 21 o superior
* Maven
* Graphviz

Para verificar Graphviz:

```bash
dot -V
```

---

## Compilar el proyecto

Desde la carpeta raíz del proyecto:

```bash
mvn clean compile
```

---

## Generar el JAR

```bash
mvn clean package
```

El JAR ejecutable se genera en:

```txt
target/GoLite-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Ejecutar el proyecto desde Maven

```bash
mvn exec:java
```

---

## Ejecutar el JAR

```bash
java -jar target/GoLite-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Funcionalidades del IDE

El IDE permite:

* Crear archivos nuevos.
* Abrir archivos `.glt`.
* Guardar archivos `.glt`.
* Ejecutar código GoLite.
* Mostrar consola de salida.
* Mostrar línea y columna actual del cursor.
* Generar reportes.

---

## Reportes implementados

El proyecto genera los siguientes reportes:

### Tabla de Tokens

Muestra los tokens reconocidos por el analizador léxico:

* Lexema
* Tipo de token
* Línea
* Columna

### Reporte de Errores

Muestra errores detectados durante el análisis y ejecución:

* Errores léxicos
* Errores sintácticos
* Errores semánticos

### Tabla de Símbolos

Muestra información de símbolos encontrados en el programa:

* Variables
* Funciones
* Structs
* Tipo
* Categoría
* Ámbito
* Línea
* Columna

### Reporte AST

Genera una representación gráfica del Árbol de Sintaxis Abstracta.

Archivos generados:

```txt
reportes/ast.dot
reportes/ast.png
```

---

## Características del lenguaje implementadas

### Tipos primitivos

```go
int
float64
string
bool
rune
nil
```

### Declaración de variables

```go
var x int = 10
y := 20
```

### Asignaciones

```go
x = 15
x += 5
x -= 2
```

### Operadores aritméticos

```go
+
-
*
/
%
```

### Operadores relacionales

```go
==
!=
<
>
<=
>=
```

### Operadores lógicos

```go
&&
||
!
```

### Estructuras de control

#### If / Else

```go
if x > 10 {
    fmt.Println("Mayor")
} else {
    fmt.Println("Menor o igual")
}
```

#### For

```go
i := 1
for i <= 5 {
    fmt.Println(i)
    i++
}
```

#### Switch

```go
opcion := 2

switch opcion {
    case 1:
        fmt.Println("Uno")
    case 2:
        fmt.Println("Dos")
    default:
        fmt.Println("Otro")
}
```

---

## Funciones

El lenguaje permite declarar y llamar funciones.

```go
func sumar(a int, b int) int {
    return a + b
}

func main() {
    resultado := sumar(10, 20)
    fmt.Println(resultado)
}
```

También permite funciones sin valor de retorno:

```go
func saludar(nombre string) {
    fmt.Println("Hola", nombre)
}
```

---

## Funciones embebidas implementadas

### fmt.Println

```go
fmt.Println("Hola mundo")
```

### strconv.Atoi

```go
numero := strconv.Atoi("100")
```

### strconv.ParseFloat

```go
decimal := strconv.ParseFloat("25.5")
```

### reflect.TypeOf

```go
fmt.Println(reflect.TypeOf(numero))
```

### len

```go
nums := []int{10,20,30}
fmt.Println(len(nums))
```

### append

```go
nums = append(nums,40)
```

### slices.Index

```go
fmt.Println(slices.Index(nums,20))
```

### strings.Join

```go
palabras := []string{"Hola","Dalio","Miranda"}
fmt.Println(strings.Join(palabras," "))
```

---

## Slices

Se implementó soporte básico para slices.

```go
func main() {
    nums := []int{10,20,30}

    fmt.Println(len(nums))
    fmt.Println(nums[1])

    nums = append(nums,40)

    fmt.Println(len(nums))
    fmt.Println(nums[3])
}
```

---

## Matrices básicas

Se implementó soporte básico para matrices utilizando slices de slices.

```go
func main() {
    fila1 := []int{1,2}
    fila2 := []int{3,4}

    matriz := [][]int{fila1,fila2}

    fmt.Println(len(matriz))
    fmt.Println(matriz[0][0])
    fmt.Println(matriz[1][1])
}
```

---

## Structs

Se implementó soporte básico para structs.

Nota: en esta implementación, los campos dentro del struct se separan por coma.

```go
type Persona struct {
    nombre string, edad int
}

func main() {
    p := Persona{nombre: "Dalio", edad: 24}

    fmt.Println(p.nombre)
    fmt.Println(p.edad)
}
```

---

## Manejo de errores

El proyecto cuenta con manejo de errores léxicos, sintácticos y semánticos.

Ejemplo de error semántico:

```go
func main() {
    x := 10
    y := z + 5

    fmt.Println(x)
}
```

El sistema detecta que la variable `z` no ha sido declarada.

También se implementó recuperación básica de errores para que el programa pueda continuar ejecutando instrucciones válidas cuando sea posible.

---

## Prueba general

Este programa prueba funciones, estructuras de control, funciones embebidas, slices, matrices y structs.

```go
type Persona struct {
    nombre string, edad int
}

func sumar(a int, b int) int {
    return a + b
}

func saludar(nombre string) {
    fmt.Println("Hola", nombre)
}

func main() {
    fmt.Println("=== PRUEBA GENERAL GOLITE ===")

    x := 10
    y := 20
    z := sumar(x, y)

    fmt.Println("Suma:", z)

    if z > 25 {
        fmt.Println("if/else: z es mayor que 25")
    } else {
        fmt.Println("if/else: z no es mayor que 25")
    }

    i := 1
    for i <= 3 {
        fmt.Println("for i:", i)
        i++
    }

    opcion := 2
    switch opcion {
        case 1:
            fmt.Println("switch: uno")
        case 2:
            fmt.Println("switch: dos")
        default:
            fmt.Println("switch: otro")
    }

    numero := strconv.Atoi("100")
    decimal := strconv.ParseFloat("25.5")

    fmt.Println("Atoi:", numero)
    fmt.Println("ParseFloat:", decimal)
    fmt.Println("TypeOf numero:", reflect.TypeOf(numero))
    fmt.Println("TypeOf decimal:", reflect.TypeOf(decimal))

    nums := []int{10,20,30}
    fmt.Println("len nums:", len(nums))
    fmt.Println("nums[1]:", nums[1])

    nums = append(nums,40)
    fmt.Println("len nums append:", len(nums))
    fmt.Println("nums[3]:", nums[3])
    fmt.Println("slices.Index 20:", slices.Index(nums,20))
    fmt.Println("slices.Index 99:", slices.Index(nums,99))

    palabras := []string{"Hola","Dalio","Miranda"}
    fmt.Println(strings.Join(palabras," "))
    fmt.Println(strings.Join(palabras,"-"))

    fila1 := []int{1,2}
    fila2 := []int{3,4}
    matriz := [][]int{fila1,fila2}

    fmt.Println("len matriz:", len(matriz))
    fmt.Println("matriz[0][0]:", matriz[0][0])
    fmt.Println("matriz[1][1]:", matriz[1][1])

    p := Persona{nombre: "Dalio", edad: 24}

    fmt.Println("struct nombre:", p.nombre)
    fmt.Println("struct edad:", p.edad)

    saludar(p.nombre)

    fmt.Println("=== FIN PRUEBA ===")
}
```

Salida esperada:

```txt
=== PRUEBA GENERAL GOLITE ===
Suma: 30
if/else: z es mayor que 25
for i: 1
for i: 2
for i: 3
switch: dos
Atoi: 100
ParseFloat: 25.5
TypeOf numero: int
TypeOf decimal: float64
len nums: 3
nums[1]: 20
len nums append: 4
nums[3]: 40
slices.Index 20: 1
slices.Index 99: -1
Hola Dalio Miranda
Hola-Dalio-Miranda
len matriz: 2
matriz[0][0]: 1
matriz[1][1]: 4
struct nombre: Dalio
struct edad: 24
Hola Dalio
=== FIN PRUEBA ===
```

---

## Estructura general del proyecto

```txt
GoLite/
├── src/
│   └── main/
│       ├── java/
│       │   ├── analisis/
│       │   └── com/golite/
│       │       ├── ast/
│       │       ├── gui/
│       │       ├── interpreter/
│       │       └── reports/
│       └── jflex/
├── reportes/
│   ├── ast.dot
│   └── ast.png
├── target/
├── pom.xml
└── README.md
```

---
## Structs

Se implementó soporte para definición, creación, acceso y modificación de atributos en structs.

Nota: en esta implementación, los campos dentro del struct se separan por coma.

```go
type Persona struct {
    nombre string, edad int
}

func main() {
    p := Persona{nombre: "Dalio", edad: 24}

    fmt.Println(p.nombre)
    fmt.Println(p.edad)

    p.nombre = "Carlos"
    p.edad = 25

    fmt.Println(p.nombre)
    fmt.Println(p.edad)
}
```

Salida esperada:

```txt
Dalio
24
Carlos
25
```

---

## Funciones con structs

Se implementó soporte para funciones que reciben structs como parámetros.
Debido a que internamente los structs se manejan como referencias, una función puede modificar sus atributos y los cambios se mantienen fuera de la función.

```go
type Persona struct {
    nombre string, edad int
}

func cambiarNombre(p Persona, nuevo string) {
    p.nombre = nuevo
}

func cumplirAnios(p Persona) {
    p.edad = p.edad + 1
}

func main() {
    p := Persona{nombre: "Dalio", edad: 24}

    fmt.Println(p.nombre)
    fmt.Println(p.edad)

    cambiarNombre(p, "Carlos")
    cumplirAnios(p)

    fmt.Println(p.nombre)
    fmt.Println(p.edad)
}
```

Salida esperada:

```txt
Dalio
24
Carlos
25
```

---

## Manejo de errores léxicos

El analizador léxico detecta caracteres no reconocidos y los agrega al reporte de errores.

Ejemplo:

```go
func main() {
    x := 10

    @
    $
    ¬

    fmt.Println(x)
}
```

Salida esperada:

```txt
10

=== Errores Lexicos ===
[Lexico] Caracter no reconocido: '@'
[Lexico] Caracter no reconocido: '$'
[Lexico] Caracter no reconocido: '¬'
```

Estos errores también se muestran en el reporte de errores de la interfaz gráfica.


## Notas finales

El proyecto implementa una versión funcional del lenguaje GoLite con IDE gráfico, ejecución de código, generación de reportes, manejo de errores y estructuras avanzadas como slices, matrices básicas y structs.

La generación del AST requiere Graphviz instalado y disponible en el PATH del sistema.
