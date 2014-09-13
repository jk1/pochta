package org.jtalks.pochta.store

import java.io.File

/**
 * @author Artem Khvastunov
 */

/**
 *
 */
fun File.cleanDirectory() {
    directory.listFiles()?.forEach({ it.delete() })
}

/**
 *
 */
fun File.listFilesByLasModified(): List<File>? {
    return directory.listFiles()?.toSortedListBy({ it.lastModified() })
}

/**
 *
 */
fun File.getFirstModified(): File? {
    return listFilesByLasModified()?.get(0)
}

/**
 *
 */
fun File.file(name: String): File {
    return File(directory.path + "/$name")
}
