package br.com.efdparser;

import br.com.efdparser.cli.CliOptions;
import br.com.efdparser.model.EfdSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        CliOptions options;
        try {
            options = CliOptions.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
            System.exit(1);
            return;
        }

        var parser = new EfdParser();
        EfdSummary summary;
        try {
            summary = parser.parse(Path.of(options.inputFile()), options.period());
        } catch (Exception e) {
            System.err.println("Failed to parse EFD file: " + e.getMessage());
            System.exit(2);
            return;
        }

        var mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

        try {
            if (options.outputFile() != null) {
                mapper.writeValue(new File(options.outputFile()), summary);
                System.out.println("Summary written to: " + options.outputFile());
                if (summary.warnings > 0) {
                    System.out.println("Warnings: " + summary.warnings
                        + " lines were skipped (check logs for details)");
                }
            } else {
                System.out.println(mapper.writeValueAsString(summary));
            }
        } catch (Exception e) {
            System.err.println("Failed to write output: " + e.getMessage());
            System.exit(3);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar efd-parser.jar --input <file.txt> [--output <summary.json>] [--period <YYYY-MM>]");
        System.err.println();
        System.err.println("  --input   Path to EFD Fiscal text file (required)");
        System.err.println("  --output  Path for the output JSON file (optional; default: stdout)");
        System.err.println("  --period  Competency period to validate against, format YYYY-MM (optional)");
    }
}
