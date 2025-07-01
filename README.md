# Jar CFG Generator

## Sample
### Intra-class call flow graph.
Class-to-class references are not depicted.
Only method node / field node accesses are the target.
> Black node = method node
> 
> Blue node = field node
![sample-class](https://github.com/user-attachments/assets/f4bd55ec-977e-4c8e-9d18-b13a643b0973)

### Inter-class call flow graph.
Only the classes are depicted, others are omitted.
> Green node = direct access to a class (supreclass, type cast, instantiation, ...)
> 
> Black node = calls the method of the class
> 
> Blue node = access the field of the class
![sample-jar](https://github.com/user-attachments/assets/9e8ff3c2-ed4e-402e-a079-e23e685619b7)

## Specification
* Supports Java >= 17.
* Works fine on macOS, possibly on *NIX, not tested on Windows.

## Usage
If you want to check the method, field, class access from a single class (intra-class), use ```class``` mode.
```bash
java -jar CFGGenerator.java \
--mode    class \
--in      target.jar \
--out     output/output.png \
--target  com/target/package/ClassName
```
Or if you want to see the class references(inter-class), use ```jar``` mode instead.
```bash
java -jar CFGGenerator.java \
--mode    jar \
--in      target.jar \
--out     output/output.png \
--target  com/target/package
```

### Options

| Key | Usage | Description |
|--------|--------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| mode | --mode ```{jar or class}``` | define the mode to be applied. ```jar``` for multiple classes, ```class``` for a single class. |
| in | --in ```{jar file path}``` | define input file, to generate CFG. |
| out | --out ```{output directory}/output.png``` | define output file path. this must be the name with extension (.png) |
| target | --target ```{class name or the package name}``` | define the class name or package name. when jar mode is activated, you can input the multiple package name, comma separated. |
| anonymous | --anonymous ```{true or false}``` | define whether to include anonymous classes or not. default is false. |


For more options, refer ```--help``` description.

> In ```jar``` mode, ```target``` can be omitted if you want to draw the whole relation graph.
> Note that in this case, the task may takes very long time, and may end up with exception.

## Build
To build this repository, gradle is required.

Run the gradle task jar to build the jar file. or run the command below to build the jar file.
```bash
./gradlew jar
```
In this way, the jar file will be generated at {PROJECT_ROOT}/build/libs/.

Make sure everything is packed properly.

