Contributing to FractalZoo
==========================
Contributions are always welcome. Currently the easiest way is 
to add new fractal classes. Either to the package com.draabek.fractal.canvas.instance
as subclasses of CanvasFractal (BitmapDrawFractal), or to 
app/src/main/assets/ as glsl files. The fractal then needs to be
added to src/main/res/raw/fractallist.json to be visible to the
app (no fancy automatic class loading, not yet anyway). 

There can be parameters, see fractallist.json, of which centerX, centerY,
and scale are special - they will provide the respective functionalities
to the user. They are not always used for now, so can be omitted (but
it should be easy for the code incorporate them). 

Note that it is not clear what should still be considered fractal and
what should not. If it is pretty enough, it does not matter:)