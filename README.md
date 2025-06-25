# JavaDataMind

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-23-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.8+-green.svg)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)

A sophisticated Java-based desktop application for data visualization and analysis with a modern dark theme UI. JavaDataMind provides powerful tools for importing, managing, and visualizing datasets with advanced statistical analysis capabilities.

## 🚀 Features

### 📊 Data Management
- **Multi-format Support**: Import CSV files with validation
- **Secure User Authentication**: Login system with user profiles
- **Dataset Management**: Add, edit, delete, and organize datasets
- **Color-coded Datasets**: Visual organization with customizable colors
- **Real-time Search & Filtering**: Quick dataset discovery

### 📈 Advanced Visualization
- **Multiple Chart Types**:
  - Bar Charts (Standard & Stacked)
  - Line Charts & Area Charts
  - Pie Charts & Scatter Plots
  - Time Series Analysis
  - Histograms
- **Dynamic Chart Generation**: Real-time visualization based on column selection
- **Interactive Charts**: Powered by JFreeChart with zoom and pan capabilities
- **Smart Data Type Detection**: Automatic recognition of numeric, text, and date/time columns

### 🧮 Statistical Analysis
- **Trend Analysis Engine**: Calculate statistical trends and patterns
- **Comprehensive Statistics**:
  - Mean, Median, Mode
  - Quartiles (Q1, Q3)
  - Variance and Standard Deviation
  - Linear Regression Slope
  - Range and Trend Direction
- **Data Export**: Export analysis results to CSV format

### 🎨 Modern UI/UX
- **FlatLaf Dark Theme**: Modern, professional appearance
- **Responsive Design**: Adaptive layout for different screen sizes
- **Component Navigation**: Tab-based interface with intuitive controls
- **Visual Feedback**: Hover effects and smooth transitions
- **Custom Components**: Placeholder text fields and styled buttons

## 🛠️ Technology Stack

### Core Technologies
- **Java 23**: Latest Java features and performance improvements
- **Maven**: Dependency management and build automation
- **SQLite**: Lightweight embedded database for data persistence
- **Swing**: Native Java GUI framework

### Key Dependencies
```xml
<!-- UI Framework -->
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>2.3</version>
</dependency>

<!-- Data Visualization -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.3</version>
</dependency>

<!-- CSV Processing -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.42.0.0</version>
</dependency>
```

## 📦 Installation & Setup

### Prerequisites
- **Java 23** or higher
- **Maven 3.8+** for building
- **Operating System**: Windows 10+, macOS 10.14+, or Linux

### Quick Start
1. **Clone the repository**:
   ```bash
   git clone https://github.com/r04nx/JavaDataMind.git
   cd JavaDataMind
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.javadata.Main"
   ```

### Alternative: JAR Execution
1. **Build JAR with dependencies**:
   ```bash
   mvn clean package
   ```

2. **Run the JAR**:
   ```bash
   java -jar target/JavaDataMind_1-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## 🎯 Usage Guide

### Getting Started
1. **Launch Application**: Run the main class or JAR file
2. **Login**: Use the authentication dialog (create new user or login)
3. **Import Data**: Navigate to "Datasources" tab and click "Add Dataset"
4. **Select File**: Choose a CSV file from your system
5. **Configure Dataset**: Set name, type, and color for visual organization

### Creating Visualizations
1. **Select Dataset**: Go to "Visualizer" tab and choose your dataset
2. **Choose Columns**: Select columns for X and Y axes
3. **Pick Chart Type**: Choose from available visualization options
4. **Generate**: Click "Generate Visualization" to create charts
5. **Analyze**: Use "Analyze Trends" for statistical insights

### Data Management Features
- **Search**: Use the search bar to filter datasets by name
- **Edit**: Modify dataset properties and colors
- **Delete**: Remove datasets (includes file cleanup)
- **Export**: Save analysis results or raw data to CSV

## 🏗️ Architecture

### Package Structure
```
com.javadata/
├── Main.java                    # Application entry point
├── analysis/
│   └── TrendAnalyzer.java      # Statistical analysis engine
├── chart/
│   └── ChartUtils.java         # Chart generation utilities
├── data/
│   ├── DatabaseManager.java    # SQLite database operations
│   └── DatasetManager.java     # Dataset CRUD operations
├── model/
│   ├── Dataset.java            # Dataset entity model
│   └── UserProfile.java        # User entity model
├── service/
│   └── UserService.java        # User management services
├── ui/
│   ├── Dashboard.java          # Main application window
│   ├── DataSourcePanel.java    # Dataset management UI
│   ├── VisualizerPanel.java    # Visualization interface
│   ├── LoginDialog.java        # Authentication UI
│   ├── SettingsPanel.java      # User preferences
│   ├── HelpAboutDialog.java    # Help information
│   └── components/             # Custom UI components
└── util/
    └── DataTypeDetector.java   # Automatic data type detection
```

### Key Components

#### 🗄️ Database Layer
- **DatabaseManager**: Singleton pattern for SQLite connection management
- **Automatic Schema Creation**: Tables created on first run
- **Transaction Management**: Ensures data consistency

#### 📊 Data Processing
- **DatasetManager**: Handles file operations and database persistence
- **TrendAnalyzer**: Performs statistical calculations and trend analysis
- **DataTypeDetector**: Automatically identifies column data types

#### 🎨 User Interface
- **Dashboard**: Central hub with navigation and card layout
- **Panel-based Architecture**: Modular UI components
- **Custom Components**: Enhanced text fields and buttons

## 🔧 Configuration

### Database Configuration
The application automatically creates a SQLite database (`javadatamind.db`) with the following schema:

```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE datasets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    color TEXT NOT NULL,
    uploaded_by TEXT NOT NULL,
    file_path TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### File Storage
- **Data Directory**: `./data/` (created automatically)
- **File Naming**: `{dataset_name}_{timestamp}.csv`
- **Size Limit**: 10MB per file
- **Supported Formats**: CSV files with proper headers

## 🚦 Testing

### Running Tests
```bash
# Run unit tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Sample Data
The project includes sample datasets in the `data/` directory:
- `Car Sales Data_20241129151004.csv`: Automotive sales data
- `Order Sample_20241126041038.csv`: E-commerce order data

## 🤝 Contributing

### Development Setup
1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/new-feature`
3. **Commit** changes: `git commit -am 'Add new feature'`
4. **Push** to branch: `git push origin feature/new-feature`
5. **Submit** a Pull Request

### Code Style
- Follow **Java naming conventions**
- Use **meaningful variable names**
- Add **JavaDoc comments** for public methods
- Maintain **consistent indentation** (4 spaces)

### Pull Request Guidelines
- Include description of changes
- Add tests for new functionality
- Ensure all existing tests pass
- Update documentation as needed

## 📈 Performance

### System Requirements
- **Memory**: 512MB RAM minimum, 1GB recommended
- **Storage**: 100MB available space
- **CPU**: Any modern processor (dual-core recommended)
- **Java**: Version 23 or higher

### Optimization Features
- **Lazy Loading**: Charts generated on-demand
- **Memory Management**: Efficient data structure usage
- **File Streaming**: Large CSV files processed incrementally
- **Database Indexing**: Optimized queries for fast retrieval

## 🐛 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check Java version
java --version

# Verify classpath
java -cp "target/*" com.javadata.Main
```

#### Database Connection Issues
- Ensure write permissions in application directory
- Check for file locks on `javadatamind.db`
- Restart application if corruption suspected

#### Visualization Problems
- Verify CSV file format (headers in first row)
- Check for numeric data in selected columns
- Ensure sufficient data points for meaningful charts

### Debug Mode
Enable detailed logging by setting JVM property:
```bash
java -Dcom.javadata.debug=true -jar JavaDataMind.jar
```

## 📚 Documentation

### API Documentation
Generate JavaDoc documentation:
```bash
mvn javadoc:javadoc
# View at target/site/apidocs/index.html
```

### User Manual
Detailed user guide available in the application:
- **Help Menu**: Access from Dashboard → Help/About
- **Tooltips**: Hover over UI elements for quick help
- **Error Messages**: Descriptive feedback for user actions

## 🔐 Security

### User Authentication
- **Password Hashing**: Secure storage using built-in Java security
- **Session Management**: Automatic logout on application close
- **Input Validation**: Protection against malicious file uploads

### Data Privacy
- **Local Storage**: All data remains on user's machine
- **No Network Communication**: Offline-first architecture
- **File Permissions**: Restricted access to data directory

## 🛣️ Roadmap

### Version 2.0 (Planned)
- [ ] **Multi-sheet Excel Support**: Import XLSX files
- [ ] **Real-time Data Feeds**: Connect to live data sources
- [ ] **Advanced Analytics**: Machine learning integration
- [ ] **Cloud Sync**: Optional cloud backup and sync
- [ ] **Plugin Architecture**: Custom visualization plugins

### Version 1.1 (Upcoming)
- [ ] **Export to Images**: Save charts as PNG/SVG
- [ ] **Data Transformation**: Built-in data cleaning tools
- [ ] **Multiple Themes**: Light theme option
- [ ] **Keyboard Shortcuts**: Power user features

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Authors

- **r04nx** - *Initial work* - [GitHub Profile](https://github.com/r04nx)

## 🙏 Acknowledgments

- **JFreeChart Team** - Excellent charting library
- **FlatLaf** - Modern Swing look and feel
- **OpenCSV** - Robust CSV processing
- **SQLite** - Reliable embedded database
- **Maven Community** - Build and dependency management

## 📞 Support

### Getting Help
- **Issues**: [GitHub Issues](https://github.com/r04nx/JavaDataMind/issues)
- **Discussions**: [GitHub Discussions](https://github.com/r04nx/JavaDataMind/discussions)
- **Email**: [Contact Developer](mailto:support@javadatamind.com)

### Feature Requests
We welcome feature requests! Please use the GitHub Issues template and label as "enhancement".

---

**⭐ Star this repository if you find it useful!**

*JavaDataMind - Making data visualization accessible to everyone.*
