package Dominos;

import java.util.Collections;
import java.util.Stack;

/**
 * Data structure for the boneyard
 * is initialized with all possible domino
 */
public class BoneYard extends Stack<Domino> {
    BoneYard(int maxDots) {
        for (int i = 0; i <= maxDots; i++) {
            for (int j = i; j <= maxDots; j++) {
                this.add(new Domino(i, j));
            }

        }
        Collections.shuffle(this);
    }
}
