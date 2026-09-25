package app.tuxguitar.android.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class TGStreamUtil private constructor() {
    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun getInputStream(input: InputStream): InputStream {
            val output = ByteArrayOutputStream()
            write(input, output)
            return ByteArrayInputStream(output.toByteArray())
        }

        @JvmStatic
        @Throws(IOException::class)
        fun write(input: InputStream, output: OutputStream) {
            while (true) {
                val read = input.read()
                if (read == -1) {
                    break
                }
                output.write(read)
            }
            input.close()
            output.close()
            output.flush()
        }
    }
}
