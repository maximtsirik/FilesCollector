# FileCollector

FileCollector is a command-line Java application designed to selectively copy files from one directory to another based on specified file extensions. It's a powerful tool for efficient file management in a variety of use cases.

## Features

- **Selective File Copying**: Filter files by extensions for targeted copying.
- **Depth Control**: Copy files from specified directory depth levels.
- **Efficient and Fast**: Optimized for fast and reliable operation.
- **User-Friendly Error Handling**: Provides clear feedback during errors or issues.
- **Cross-Platform Compatibility**: Works on any platform that supports Java.

## Requirements

- Java 8 or higher.

## Getting Started

### Installation

Download the latest version of FileCollector from the releases page. No additional installation is required.

### Usage

Run FileCollector using the following command structure:

```bash
java -jar FileCollector.jar --source [source_directory] --dest [destination_directory] -e [file_extension]...
```

Command-Line Arguments
- **extension** or -e: Specify file extensions to filter (e.g., .png, .pdf). You can include multiple extensions.
- **source:** Set the source directory.
- **dest:** Set the destination directory.
- **depth:** Define the depth of subdirectories to search (optional).

Examples

Copy PNG and PDF files from the Downloads folder to the img folder:

```bash
java -jar FileCollector.jar --source 'C:\Users\user\Downloads' --dest 'D:\img' -e '.png' -e '.pdf'
```

Copy DOCX files from Documents to the backup folder, only from the top two levels of subdirectories:

```bash
java -jar FileCollector.jar --source 'C:\Users\user\Documents' --dest 'D:\backup' -e '.docx' --depth 2
```

### Best Practices and Warnings

- Avoid running the application with system folders or directories containing system files (like RECYCLE.BIN).
- Ensure you have appropriate read/write permissions for the specified directories.
- File extensions are case-sensitive; ensure they are specified correctly.

### Troubleshooting
- **Permissions:** Check read/write permissions for directories.
- **Large Directories:** For large directories, consider increasing Java's heap size or running in batches.
- **File Extensions:** Ensure correct case sensitivity for file extensions.

### Contributing

Contributions to FileCollector are welcome! Please refer to the contributing guidelines for detailed information.

License
FileCollector is released under the MIT License.

### Contact
For support, feedback, or queries, please contact **maximtsirik@gmail.com**