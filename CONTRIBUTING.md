Contributing to FractalZoo
==========================
Contributions are always welcome. You can pick an issue or create one
and resolve it with a pull request. Just please try to stick to standard
Java code style, maybe even adjust neighboring lines if they do not look
good. But no pull requests just for the sake of pretty code.

Currently the easiest way is to add new fractal classes. Either as
subclasses of CanvasFractal (easiest, least computationally effective), 
or to app/src/main/assets/ as glsl files.
The fractal then needs to be added to src/main/res/raw/fractallist.json
to be visible to the app (no automatic class loading, not yet anyway).

There can be parameters, see fractallist.json, of which centerX, centerY,
and scale are special - they will provide the respective functionalities
to the user. They are not always used for now, so can be omitted (but
it should be easy for the code incorporate them). 

Note that it is not clear what should still be considered fractal and
what should not. If it is pretty enough, it does not matter:)
