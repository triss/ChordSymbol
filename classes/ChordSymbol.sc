// Helper functions for describing chord using traditional language
ChordSymbol {
    // a list of traditional chord shapes
    classvar <shapes;

    *initClass {
        shapes = (
            major:          [0, 4, 7],
            minor:          [0, 3, 7],
            major7:         [0, 4, 7, 11],
            dom7:           [0, 4, 7, 10],
            minor7:         [0, 3, 7, 10],
            aug:            [0, 4, 8],
            dim:            [0, 3, 6],
            dim7:           [0, 3, 6, 9],
            '1':            [0],
            '5':            [0, 7],
            plus:           [0, 4, 8],
            sharp5:         [0, 4, 8],
            msharp5:        [0, 3, 8],
            sus2:           [0, 2, 7],
            sus4:           [0, 5, 7],
            '6':            [0, 4, 7, 9],
            m6:             [0, 3, 7, 9],
            '7sus2':        [0, 2, 7, 10],
            '7sus4':        [0, 5, 7, 10],
            '7flat5':       [0, 4, 6, 10],
            m7flat5:        [0, 3, 6, 10],
            '7sharp5':      [0, 4, 8, 10],
            m7sharp5:       [0, 3, 8, 10],
            '9':            [0, 4, 7, 10, 14],
            m9:             [0, 3, 7, 10, 14],
            m7sharp9:       [0, 3, 7, 10, 14],
            maj9:           [0, 4, 7, 11, 14],
            '9sus4':        [0, 5, 7, 10, 14],
            '6by9':         [0, 4, 7, 9, 14],
            m6by9:          [0, 3, 9, 7, 14],
            '7flat9':       [0, 4, 7, 10, 13],
            m7flat9:        [0, 3, 7, 10, 13],
            '7flat10':      [0, 4, 7, 10, 15],
            '9sharp5':      [0, 1, 13],
            m9sharp5:       [0, 1, 14],
            '7sharp5flat9': [0, 4, 8, 10, 13],
            m7sharp5flat9:  [0, 3, 8, 10, 13],
            '11':           [0, 4, 7, 10, 14, 17],
            m11:            [0, 3, 7, 10, 14, 17],
            maj11:          [0, 4, 7, 11, 14, 17],
            '11sharp':      [0, 4, 7, 10, 14, 18],
            m11sharp:       [0, 3, 7, 10, 14, 18],
            '13':           [0, 4, 7, 10, 14, 17, 21],
            m13:            [0, 3, 7, 10, 14, 17, 21]
        );

        // set up some alternative names for shapes
        shapes.m = shapes.minor;
        shapes[\M] = shapes.major;
        shapes[\7] = shapes.dom7;
        shapes[\M7] = shapes.major7;
        shapes[\m7] = shapes.minor7;
        shapes.augmented = shapes.aug;
        shapes.diminished = shapes.dim;
        shapes.diminished7 = shapes.dim7
    }

    // converts chords in array to notes in an array
    *noteProgression { |array| 
        ^array.collect { |c| this.asNotes(c) };
    }

    // converts chord names in array to degrees of scale in an array
    *degreeProgression { |array scale| 
        ^array.collect { |c| this.asDegrees(c, scale) };
    }

    // outputs notes in a named chord to degrees of scale in an array
    *asDegrees { |name scale stepsPerOctave=12|
        ^if(name.isKindOf(Symbol) or: { name.isKindOf(String) } 
            and: { name != \ } and: { name != \rest }
        ) {
            scale = scale ?? { Scale.major.degrees };

            ChordSymbol.asNotes(name).asArray.collect { |n| 
                // TODO when next version of SC comes out use keyToDegree
                n.keyToDegree2(scale, stepsPerOctave);
            }
        } {
            name;
        }
    }

    // outputs notes in a named chord as notes
    *asNotes { |name|
        ^if(name.isKindOf(Symbol) or: { name.isKindOf(String) } 
            and: { name != \ } and: { name != \rest }
        ){
            var over, chord;

            name = name.asString;

            // lop off the inversion if specified
            if(name.contains("_")) {
                #name, over = name.split($\_);
                over = NoteSymbol(over);
            };

            // if we know the chord name return it
            chord = if(shapes.includesKey(name.asSymbol)) {
                shapes[name.asSymbol];
            } {
                var shape, root, noteNameLength = 1;

                // parse chord name out of string
                shape = shapes[name.drop(1).asSymbol] 
                    ?? { noteNameLength = 2; shapes[name.drop(2).asSymbol] }
                    ?? { noteNameLength = 3; shapes[name.drop(3).asSymbol] }
                    ?? { shapes.major };

                // use the remainder of the string as the root note
                root = NoteSymbol(name.keep(noteNameLength));

                // if an inversion was specified
                if(over.notNil) {
                    var octaveShift = 0;

                    // shift notes up an octave if root is higher than new base
                    if(over < root) { octaveShift = 12 };

                    // iterate over the notes 
                    shape = shape.collect { |note| 
                        // and if the notes are below our new lowest note
                        // move it up an octave
                        if(note < (over - root + octaveShift)) {
                            note + 12
                        } {
                            note
                        }
                    };

                    // shift notes back if shift perfomed whilst inverting
                    shape = shape - octaveShift;
                };

                root + shape;
            };

            chord.sort; 
        } {
            // return what was passed in if not a chord name
            name
        }
    }

    *new { |c|
        ^this.toNotes(c);
    }
}

NoteSymbol {
    classvar <notes;

    *initClass {
        // define note names
        notes = (c: 0, d: 2, e: 4, f: 5, g: 7, a: 9, b: 11);

        // bung in all the sharps and flats
        notes.keysValuesDo { |name val| 
            notes[(name ++ \s).asSymbol] = val + 1;
            notes[(name ++ \b).asSymbol] = val - 1;
        };
    }

    *new { |name|
        ^if(name.isKindOf(Symbol) or: { name.isKindOf(String) } 
            and: { name != \ } and: { name != \rest }
        ) {
            var octaveShift = 0;
            name = name.asString.toLower;

            // if octave specified chop it off and shift note
            if(name.last.isDecDigit) {
                octaveShift = name.last.digit * 12 + 12; 
                name = name.drop(-1);
            };

            notes[name.asSymbol] + octaveShift;
        } {
            name;
        };
    }

    *noteName { |n| ^notes.findKeyForValue(n % 12) }
}

+ Symbol {
    asNotes { ^ChordSymbol.asNotes(this) }
    asDegrees { |scale| ^ChordSymbol.asDegrees(this, scale) }
    asNote { ^NoteSymbol(this) }
}

+ SequenceableCollection {
    // convienience for converting chord names to notes
    chordProg {
        ^ChordSymbol.noteProgression(this);
    }

    chordProgDegrees { |scale|
        ^ChordSymbol.degreeProgression(this, scale);
    }

    noteProg {  
        this.collect { |name| name.asNote };
    }

    // converts a given key/note to a degree
    // TODO won't be required in next SC release see pull request #1164
    performKeyToDegree2 { |key stepsPerOctave=12|
        var nearestDegree, closestScale, sharpening, octave;

        // store away octave and wrap key inside a single one
        octave = (key / stepsPerOctave).floor;
		key = key % stepsPerOctave;

        // find the closest degree in scale for all keys in octave
        closestScale = (0..stepsPerOctave).collect { |k| 
            this.indexInBetween(k).floor 
        };

        // find the closest degree in the scale for the key
		nearestDegree = this.indexInBetween(key).floor;

        // calculate how much to sharpen our degree by
        sharpening = closestScale.indexOf(nearestDegree) - key / -10;

        ^nearestDegree + sharpening + (octave * this.size);
    }
}

+ SimpleNumber {
    // TODO won't be required in next SC release see pull request #1164
    keyToDegree2 { arg scale, stepsPerOctave=12; // collection is presumed to be sorted
       ^scale.performKeyToDegree2(this, stepsPerOctave) 
    }
}

+ Scale {
    // TODO won't be required in next SC release see pull request #1164
    performKeyToDegree2 { |degree stepsPerOctave=12| 
        ^degrees.performKeyToDegree2(degree, stepsPerOctave)
    }
}
