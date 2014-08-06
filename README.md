ChordSymbol
===========

Simple notation for chords in SuperCollider

Quickly write progressions with .chordProg: 
```
[\Cm_eb, \Fm, \Gm, \Cm_g, \Cm_eb, \Fm, \Gm_d, \Cm].chordProg;
[\C, \G_b, \F_a, \G_b].chordProg;
```
Supports multiple naming conventions for common chords:
```  
\C.asNotes              // C major -> [0, 4, 7]
\Cmajor.asNotes         // C major -> [0, 4, 7]
\CM.asNotes             // C major -> [0, 4, 7] 

\Cm.asNotes             // C minor -> [0, 3, 7]
\Em7sharp5flat9.asNotes // [ 4, 7, 12, 14, 17 ]
```
Supports sharps and flats using s and b respectively:
```
\Gs.asNotes             // G# major -> [ 8, 12, 15 ]
\Dbmaj11.asNotes        // Db major11 -> [ 1, 5, 8, 12, 15, 18 ]
```
And slash/inverted chords - just replace the slash seen in notation with an underscore
```
\C_g.asNotes            // C/g -> [ 7, 12, 16 ]
\Fm_gs.asNotes          // Fm/a -> [ 8, 12, 17 ]
\Dsus4_g.asNotes        // Dsus4/g -> [ 7, 9, 14 ]
```
It also allows the user specify the octave if required
```
\C4m7sharp9.asNotes     // [ 60, 63, 67, 70, 74 ]
\C5m7sharp9.asNotes     // [ 72, 75, 79, 82, 86 ]
\F6plus_Cs.asNotes      // [ 77, 81, 85 ]
```
It'll tell you which degrees would be in a chord shape in a given mode/scale Be careful of sharps/flat when using to produce scale degrees. This is fantastic for notating chord progressions but may not behave as expected when  transposed etc. A root note of C is always assumed:
```
\Cmajor.asDegrees                  // major scale assumed if none specified -> [0, 2, 4]
\Cminor.asDegrees                  // [0, 1.1, 4]
\Cmajor.asDegrees(Scale.minor)     // major chord in minor mode -> [0, 2.1, 4] 
\m7sharp5.asDegrees(Scale.dorian)  // [ 0, 2, 4.1, 6 ]
```
You can also get the degrees for a progression in terms of a particular mode:
```
[\Cm_eb, \Fm, \Gm, \Cm_g, \Cm_eb, \Fm, \Gm_d, \Cm].chordProgDegrees(Scale.dorian);
```
To see the suilt in list of chords say `ChordSymbol.shapes.keys`. You cam add to this with `.put`

Check chord Symbols help in SC for more info.
