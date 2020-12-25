package com.stroganovns.wiki

import java.io.*
import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream

class BZip2Archiver {

    fun unarchive(input: File, output: File) {
        SevenZip.initSevenZipFromPlatformJAR()

        val inArchive = SevenZip.openInArchive(
            ArchiveFormat.BZIP2,
            RandomAccessFileInStream(RandomAccessFile(input, "r"))
        )
        val inputStream = inArchive.simpleInterface.archiveItems
        val outputStream =  output.outputStream()

        inputStream.first().extractSlow { data ->
            outputStream.write(data)
            data.size
        }
        inArchive.close()
        outputStream.close()
    }
}