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
> Green node = direct access to a class (supreclass, type cast, instanciation, ...)
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
--mode class \
--in target.jar \
--out output/output.png \
--target com/target/package/ClassName
```
Or if you want to see the class references(inter-class), use ```jar``` mode instead.
```bash
java -jar CFGGenerator.java \
--mode jar \
--in target.jar \
--out output/output.png \
--target com/target/package
```

For more options, refer ```--help``` description.

> In ```jar``` mode, ```target``` can be omitted if you want to draw the whole relation graph.
> Note that in this case, the task may takes very long time, and may end up with exception.
