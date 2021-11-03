package _0_1.math

import java.lang.Math.pow

class Random {
    companion object{


        class XOR{
            companion object{
                var s: Int = 1
                    private set

                init{
                    this.s = s or 1;
                }
                fun gen(  ): Float {

                    this.s = this.s xor ( this.s shl 13 );
                    this.s = this.s xor ( this.s ushr 17 );
                    this.s = this.s xor ( this.s shl 5 );
                    return ((this.s / pow( 2.0, 32.0 )).toFloat() + 0.5).toFloat();
                }

                fun seed( _seed: Int) {
                    s = _seed
                }

            }

        }

    }
}