# Quick Start Guide

## Building the Plugin

1. **Build the plugin:**
   ```bash
   ./gradlew buildPlugin
   ```
   
   The plugin will be generated in `build/distributions/testsuite-runner-plugin-1.0.0.zip`

2. **Test in development mode:**
   ```bash
   ./gradlew runIde
   ```
   
   This will launch a new WebStorm instance with your plugin installed.

## Installing in WebStorm

### Method 1: From Disk
1. Build the plugin: `./gradlew buildPlugin`
2. Open WebStorm
3. Go to `Settings/Preferences → Plugins`
4. Click the gear icon ⚙️ → `Install Plugin from Disk...`
5. Select `build/distributions/testsuite-runner-plugin-1.0.0.zip`
6. Restart WebStorm

### Method 2: Development Mode
```bash
./gradlew runIde
```
This launches a sandboxed IDE with the plugin pre-installed.

## Using the Plugin

Once installed, open any TypeScript test file that uses the `TestSuite` pattern:

### Features

1. **Gutter Icons**: Green run icons appear next to:
   - `TestSuite.new()` calls (runs entire suite)
   - Individual `it()`, `test()`, or `describe()` calls

2. **Context Menu**: Right-click in a test file → "Run TestSuite"

3. **Run Configurations**: Auto-created when you run a test

### Example Test Structure

```typescript
import { TestSuite } from '@talentfinder/api/testing';

TestSuite.new('My Test Suite')
  .withModule(MyModule)
  .withMockedDate()
  .build(({ app, database }) => {
    
    it('should pass this test', async () => {
      // Click the ▶️ icon here to run just this test
      expect(true).toBe(true);
    });
    
    it('should pass another test', async () => {
      // Click the ▶️ icon here to run just this test
      expect(1 + 1).toBe(2);
    });
  });
  // Click the ▶️ icon on the TestSuite.new() line to run all tests
```

## Troubleshooting

### Plugin doesn't detect tests
- Ensure your test file uses `TestSuite.new().build()` pattern
- File must be `.ts` or `.spec.ts`
- Verify the plugin is enabled in Settings → Plugins

### Tests don't run
- Check Jest is installed: `npm list jest`
- Verify working directory in run configuration
- Check terminal output for errors

### Gutter icons not appearing
- Make sure the file is recognized as TypeScript
- Try restarting WebStorm
- Check "Settings → Editor → General → Gutter Icons" is enabled

## Development Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run in development IDE
./gradlew runIde

# Clean build
./gradlew clean

# Verify plugin structure
./gradlew verifyPlugin
```

## Requirements

- Java 17+ (for building)
- WebStorm 2023.3+ or IntelliJ IDEA Ultimate 2023.3+
- Node.js and Jest (for running tests)

## Plugin Structure

```
wsplugin/
├── src/main/
│   ├── kotlin/com/powerus/testsuiterunner/
│   │   ├── TestSuiteTestFinder.kt           # Detects TestSuite patterns
│   │   ├── markers/
│   │   │   └── TestSuiteLineMarkerProvider.kt # Adds gutter icons
│   │   ├── run/
│   │   │   ├── TestSuiteRunConfiguration*.kt  # Run configuration
│   │   │   └── TestSuiteRunner.kt             # Executes tests
│   │   └── actions/
│   │       └── RunTestSuiteAction.kt          # Context menu action
│   └── resources/META-INF/
│       └── plugin.xml                         # Plugin manifest
├── build.gradle.kts                           # Build configuration
└── README.md                                  # Documentation
```

## Support

For issues or questions:
1. Check the README.md for detailed documentation
2. Review troubleshooting section above
3. Check WebStorm's "Event Log" for plugin errors

## Next Steps

After installation:
1. Open a test file with `TestSuite.new()` pattern
2. Look for green ▶️ icons in the left gutter
3. Click an icon to run tests
4. View results in the Run tool window
