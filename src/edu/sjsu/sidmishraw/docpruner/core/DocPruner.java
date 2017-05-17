/**
 * Created by sidmishraw.
 * <p>
 * For any assistance, email: sidharth.mishra@sjsu.edu
 * <p>
 * Created 5/16/17.
 * <p>
 * Last modified 5/17/17.
 */
package edu.sjsu.sidmishraw.docpruner.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sidmishraw on 5/16/17.
 */
public class DocPruner {

	private static final String OS_NAME  = "os.name";
	private static final String MAC_OS_X = "Mac OS X";
	private static final String WINDOWS  = "Windows";
	private static final String LINUX    = "Linux";

	// folder names
	private static final String BAD_PDFS            = "badPDFs";
	private static final String BAD_GENERATED_FILES = "badGeneratedFiles";

	public static String isErrorLine(String line) {

		// example log file entry: within quotes below, no quotes in the original log file.
		// "2017-04-17 22:52:33,427 ERROR ERROR: The pdfdocument
		// /Users/sidmishraw/Documents/workspace/cs_267_project/input_pdfs/00015086.pdf
		// maybe a scanned image."
		Pattern
				pattern =
				Pattern.compile("[0-9\\-]*\\s{1}[0-9\\-\\:\\,]*\\s{1}ERROR\\s{1}ERROR\\:\\s{1" +
						"}The\\s{1" +
						"}pdfdocument\\s{1}" +
						"(.*)\\s{1}" +
						"maybe\\s{1}a\\s{1}" +
						"scanned\\s{1}image\\.");

		String pdfPath = null;

		Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {

			pdfPath = matcher.group(1);
		}

		return pdfPath;
	}

	/**
	 * Prune the docs for the given folder
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 3) {

			System.out.println("Usage: java -jar DocPruner.jar <path-to-pdfprocessor.log> "
					+ "<path-to-pdf_jsons> <path-to-pdf_grouped_jsons>");
			System.exit(0);
		}

		String pdfprocessorLogPath = args[0];
		String pdfJSONPath         = args[1];
		String pdfGroupedJSONPath  = args[2];

		// just used for testing
//		String pdfprocessorLogPath = "pdfprocessor.log";
//		String pdfGroupedJSONPath =
// "/Users/sidmishraw/Documents/workspace/cs_267_project" +
//				"/pdf_grouped_jsons";
//		String pdfJSONPath = "/Users/sidmishraw/Documents/workspace/cs_267_project" +
//				"/pdf_jsons";

		// the bad PDFs are moved to this location
		String badPDFDir            = DocPruner.BAD_PDFS;
		String badGeneratedFilesDir = DocPruner.BAD_GENERATED_FILES;

		// path to the bad PDF Directory
		Path pdfDestinationPath = Paths.get(badPDFDir).normalize();

		// move the bad generated files to their intended location
		Path jsonDestinationPath = Paths.get(badGeneratedFilesDir).normalize();

		// source files
		Path jsonPath        = Paths.get(pdfJSONPath);
		Path groupedJSONPath = Paths.get(pdfGroupedJSONPath);

		// if the destination path doesn't exist or is not a directory
		// create the directory
		if (Files.notExists(pdfDestinationPath) || !Files.isDirectory
				(pdfDestinationPath)) {

			Files.createDirectory(pdfDestinationPath);

			System.out.println("The directory " + pdfDestinationPath.getFileName()
					.toString() + " didn't exist so created it at " + System.getProperty
					("user.dir"));
		}

		// move the bad generated files to the `jsonDestinationPath`
		if (Files.notExists(jsonDestinationPath) || !Files.isDirectory
				(jsonDestinationPath)) {

			Files.createDirectory(jsonDestinationPath);

			System.out.println("The directory " + jsonDestinationPath.getFileName()
					.toString() + " didn't exist so created it at " + System.getProperty
					("user.dir"));
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new
				FileInputStream(new File(pdfprocessorLogPath))))) {

			Set<String> pdfNames = new HashSet<>();

			String line = null;

			while (null != (line = br.readLine())) {

				String pdfFilePath = isErrorLine(line);

				if (null != pdfFilePath) {

					pdfNames.add(pdfFilePath);
				}
			}

			if (pdfNames.size() > 0) {

				pdfNames.forEach(pdfName -> {

					try {

						Path sourcePath = Paths.get(pdfName);
						Path badFilePath = Paths.get(pdfDestinationPath.toString(), sourcePath
								.getFileName().toString());

						// move the bad PDF to the destination
						Files.move(sourcePath, badFilePath, StandardCopyOption
								.REPLACE_EXISTING);

						System.out.println("Successfully moved " + sourcePath.getFileName() + " to "
								+ badFilePath.toString());
					} catch (Exception e) {

						e.printStackTrace();
					}
				});

				// move the entire directory here
				Path badPDFJsons = Paths.get(jsonDestinationPath.toString(), jsonPath.getFileName
						().toString());
				Path
						badGroupedPDFJsons =
						Paths.get(jsonDestinationPath.toString(), groupedJSONPath.getFileName
								().toString());

				// move them
				Files.move(jsonPath, badPDFJsons, StandardCopyOption.REPLACE_EXISTING);

				Files.move(groupedJSONPath, badGroupedPDFJsons, StandardCopyOption
						.REPLACE_EXISTING);

				System.out.println("Successfully moved the generated files");
			} else {

				System.out.println("Nothing to move :)");
			}
		} catch (Exception e) {

			System.err.println("Log file not found" + e.getMessage());
		}
	}
}
