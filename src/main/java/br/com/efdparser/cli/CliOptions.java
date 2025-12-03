package br.com.efdparser.cli;

public record CliOptions(String inputFile, String outputFile, String period) {

    public static CliOptions parse(String[] args) {
        String input = null;
        String output = null;
        String period = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input" -> {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("--input requires a value");
                    input = args[++i];
                }
                case "--output" -> {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("--output requires a value");
                    output = args[++i];
                }
                case "--period" -> {
                    if (i + 1 >= args.length) throw new IllegalArgumentException("--period requires a value");
                    period = args[++i];
                    if (!period.matches("\\d{4}-\\d{2}")) {
                        throw new IllegalArgumentException("--period must be in YYYY-MM format, got: " + period);
                    }
                }
                default -> throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }

        if (input == null) throw new IllegalArgumentException("--input is required");
        return new CliOptions(input, output, period);
    }
}
