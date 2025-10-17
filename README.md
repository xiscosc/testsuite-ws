# TestSuite Runner Plugin for WebStorm

A WebStorm/IntelliJ IDEA plugin that enables test detection and execution for tests wrapped in custom `TestSuite` helper classes.

## Features

- **Test Detection**: Automatically detects tests wrapped in `TestSuite.new().build()` patterns
- **Gutter Icons**: Adds run icons in the editor gutter for:
  - Individual test cases (`it`, `test`, `describe`)
  - Entire test suites
- **Run Configurations**: Creates and manages run configurations for TestSuite tests
- **Context Menu**: Right-click context menu integration for running tests
- **Jest Integration**: Works seamlessly with Jest test framework

## Installation

### From Source

1. Clone this repository
2. Open the project in IntelliJ IDEA
3. Run the Gradle task: `./gradlew buildPlugin`
4. The plugin will be built in `build/distributions/`
5. In WebStorm/IntelliJ IDEA:
   - Go to `Settings/Preferences → Plugins`
   - Click the gear icon ⚙️
   - Select `Install Plugin from Disk...`
   - Choose the generated `.zip` file

### Development

To run the plugin in a development environment:

```bash
./gradlew runIde
```

## Usage

Once installed, the plugin will automatically detect test files that use the `TestSuite` pattern:

```typescript
TestSuite.new('Test Suite Name')
  .withModule(SomeModule)
  .withMockedDate()
  .build(({ app, database, now }) => {
    it('should do something', async () => {
      // test code
    });
  });
```

### Running Tests

1. **Gutter Icons**: Click the green run icon that appears next to:
   - The `TestSuite.new()` call (runs entire suite)
   - Individual `it()` or `test()` calls (runs single test)

2. **Context Menu**: Right-click anywhere in a test file and select "Run TestSuite"

3. **Run Configuration**: Create a run configuration manually via:
   - `Run → Edit Configurations...`
   - Click `+` and select `TestSuite`
   - Configure test file path and options

## Configuration

The plugin will attempt to auto-detect your Jest configuration. You can also specify:

- **Test File Path**: The spec file to run
- **Test Name**: Optional pattern to match specific tests
- **Working Directory**: Project root (auto-detected)
- **Jest Config**: Optional path to custom Jest configuration

## Requirements

- WebStorm 2023.3+ or IntelliJ IDEA Ultimate 2023.3+
- Node.js and npm installed
- Jest test framework

## Supported Test Patterns

The plugin recognizes the following patterns:

```typescript
// Test suite declaration
TestSuite.new('Suite name')
  .withModule(Module)
  .withMockedDate()
  .build(({ dependencies }) => {
    // tests here
  });

// Test cases
it('test name', async () => { });
test('test name', async () => { });
describe('group name', () => { });
```

## Development

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

### Running in IDE

```bash
./gradlew runIde
```

## Troubleshooting

### Tests not detected

- Ensure your test file uses the `TestSuite.new()` pattern
- Check that the file extension is `.ts` or `.spec.ts`
- Verify Jest is installed in your project

### Tests not running

- Check that Jest is properly configured in your project
- Verify the working directory is set correctly
- Ensure Node.js is installed and accessible

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
