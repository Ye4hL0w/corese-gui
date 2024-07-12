# Corese-gui

Corese GUI is a graphical user interface designed to facilitate the use of Corese, a semantic search engine implementing web semantic standards.

# Description

Corese, developed at INRIA, is used for experimentation and querying in the field of semantic web. Corese includes several modules and interfaces, each offering specific functionalities:

- **Corese-core**: The core engine, implements algorithms for processing and querying semantic data.
- **Corese-command**: Command-line interface for interacting with Corese through scripts and direct commands.
- **Corese-server**: Server providing access to Corese via HTTP requests, ideal for distributed environments.
- **Corese-python**: Module allowing Corese integration with Python for development and automation.
- **Corese-gui**: Enhanced graphical user interface for intuitive use of Corese.

# Corese GUI Features

- Creation, manipulation, and analysis of RDF ontologies.
- Advanced querying of RDF data with complex queries.
- User-friendly interface facilitating the visualization and modification of semantic data.
- Support for importing, serializing, reasoning, and validating RDF data.

# Corese Application - Issues and Tasks

## To Do

### Features to Implement

- **Link SHACL Validation**  
  SHACL validation needs to be integrated to enable RDF schema validation using SHACL. This feature is currently missing.

- **"Reload File" Functionality**  
  The functionality to reload a file is not working correctly and needs to be fixed. It does not meet expectations.

- **"Save As" Button Functionality**  
  The functionality for the "Save As" button is not implemented.


- **Drag and Drop in Query**  
  Drag and drop to reorganize tabs in the initial `TabPane` is not working properly. Dropping it back into the initial `TabPane` does not have the expected effect.

- **Visual Display for CONSTRUCT Queries**  
  The linkage of `CONSTRUCT` queries with visual display is not functioning. The code is commented out.

- **XML Result Display for Queries**  
  The display of results in XML format for queries does not work as expected.

- **Implementation of Settings View**  
  The settings view (`SettingsView`) is not yet implemented.

- **Link Data - Logs, RDF Rules, Stats**  
  The sections dedicated to managing logs, RDF rules, and statistics in the `DataView` are not integrated.

## Bugs

- **Bugs in Code Editor Tabs**  
  Tabs experience issues when navigating multiple times between Validation and Query tabs, for instance. This causes additional tabs to be added in the code editor. Additionally, the "+" (plusTab) in the code editor may not function correctly at times, especially after removing all tabs or performing a middle-click on the tabs.

- **Display Issue after File Insertion**  
  After inserting files into the Data tab, and if the tab is exited and then reopened, files and the graph will be correctly retained. However, the section dedicated to visualizing loaded files may display empty content at that moment.

