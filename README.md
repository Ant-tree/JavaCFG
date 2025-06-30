# Jar CFG Generator

## Sample
### Intra-class call flow graph.

### Inter-class call flow graph.

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
