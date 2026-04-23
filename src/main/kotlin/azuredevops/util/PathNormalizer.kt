package azuredevops.util

/**
 * Utility object for path normalization and matching.
 *
 * Azure DevOps thread paths are repo-relative with Unix separators (e.g., /src/main.kt).
 * Local file paths are absolute with OS-specific separators.
 *
 * This object provides utilities to normalize and match these paths.
 */
object PathNormalizer {
    /**
     * Normalizes a thread path from Azure DevOps.
     * Converts forward slashes to backslashes and removes leading slash.
     *
     * @param threadPath The path from Azure DevOps (e.g., /src/main.kt)
     * @return Normalized path (e.g., src\main.kt)
     */
    fun normalizeThreadPath(threadPath: String): String =
        threadPath
            .replace('/', '\\')
            .trimStart('\\')

    /**
     * Normalizes a local file path for comparison.
     * Ensures consistent separator usage.
     *
     * @param filePath The local file path (absolute)
     * @return Normalized file path
     */
    fun normalizeFilePath(filePath: String): String =
        filePath.replace('/', '\\')

    /**
     * Checks if a thread path matches a file path.
     * Handles differences between Azure DevOps paths and local paths.
     *
     * @param threadPath Path from Azure DevOps (repo-relative)
     * @param filePath Local file path (absolute)
     * @return true if the thread belongs to this file
     */
    fun matchThreadToFilePath(threadPath: String, filePath: String): Boolean {
        val normalizedThread = normalizeThreadPath(threadPath)
        val normalizedFile = normalizeFilePath(filePath)

        // Check if file path ends with thread path (case-insensitive)
        return normalizedFile.endsWith(normalizedThread, ignoreCase = true)
    }

    /**
     * Extracts the file name from a path.
     *
     * @param path The path (either thread or file path)
     * @return The file name without directory
     */
    fun extractFileName(path: String): String =
        path
            .replace('/', '\\')
            .substringAfterLast('\\', path)

    /**
     * Checks if a path is a valid file path (not empty, has extension).
     *
     * @param path The path to validate
     * @return true if valid file path
     */
    fun isValidFilePath(path: String?): Boolean =
        !path.isNullOrBlank() &&
            path.contains('.') &&
            !path.endsWith('/') &&
            !path.endsWith('\\')
}

/**
 * Extension functions for string path operations.
 */
object PathExtensions {
    /**
     * Ensures a path starts with a forward slash.
     */
    fun String.ensureLeadingSlash(): String =
        if (startsWith('/')) this else "/$this"

    /**
     * Removes leading and trailing slashes.
     */
    fun String.trimSlashes(): String =
        trim('/', '\\')

    /**
     * Converts a path to Unix-style (forward slashes).
     */
    fun String.toUnixPath(): String =
        replace('\\', '/')

    /**
     * Converts a path to Windows-style (backslashes).
     */
    fun String.toWindowsPath(): String =
        replace('/', '\\')
}
