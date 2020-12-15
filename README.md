# Pentaho Data Integrator DOC Plugins

This project allows you to manage documents in Pentaho's Data Integration.

Works with PDI 6.1,7.1 and 8.2.

## Building the plugins

Check out the project if you have not already done so :

        git clone git://github.com/atolcd/pentaho-doc-plugins.git
        cd pentaho-doc-plugins

Install Java 8+, Maven and PDI.

To package the plugins, run the following commands from the base project directory :

        # Create the package
        mvn clean package

The built package is target/pentaho-gis-plugins-1.0.0-bin.zip (version can differ)

**_Integration with Eclipse_**

If you want to use the [Eclipse IDE](https://eclipse.org), you can easily create projects with maven. From the root directory of the project :

        mvn eclipse:eclipse

Assume that the [M2Eclipse](http://www.eclipse.org/m2e) plugin is installed and import the project from Eclipse :

1. From the "Import" item of the "File" menu, select "Existing Projects into Workspace"
2. Next, select the root directory of the project
3. Eclipse suggests 1 projects : select it and Finish
4. You can start working

It has been tested with Eclipse Luna and Mars.

## Installing/upgrading the module

**_Method 1 : Pentaho's Marketplace installation_**

In PDI, open the _Marketplace_ from the help menu. Select "PDI DOC Plugins" and click "Install this plugin".
After the installation, you need to restart PDI.

When a newer version will be available, you will see an "Upgrade to XXX" button at the right of "Uninstall this plugin" button. Don't use it.
Proceed in two steps : first use "Uninstall this plugin" then start a fresh installation.

**_Method 2 : Manual installation_**

Extract the content of pentaho-doc-plugins-VERSION-bin.zip in \${PENTAHO_HOME}/plugins.
Example of extraction from the root directory of the project :

        wget https://github.com/atolcd/pentaho-doc-plugins/releases/download/v1.0.0/pentaho-doc-plugins-1.0.0-bin.zip
        unzip pentaho-doc-plugins-1.0.0-bin.zip -d ${PENTAHO_HOME}/plugins

To upgrade the plugin, delete files you added before and start a fresh installation.

## Using the plugins

You will find new elements in "Document management"'s directory :

- Metadata writer
- PDF Merge
- Text report
- PDF Conversion

Find how to use them in the examples in the `samples` folder.

## Contributing

**_Reporting bugs_**

1. First check if the version you used is the last one
2. Next check if the issue has not ever been described in the [issues tracker](https://github.com/atolcd/pentaho-doc-plugins/issues)
3. You can [create the issue](https://github.com/atolcd/pentaho-doc-plugins/issues/new)

**_Submitting a Pull Request_**

1. Fork the repository on GitHub
2. Clone your repository (`git clone https://github.com/XXX/pentaho-doc-plugins.git && cd pentaho-doc-plugins`)
3. Create a local branch that will support your dev (`git checkout -b a-new-dev`)
4. Commit changes to your local branch branch (`git commit -am "Add a new dev"`)
5. Push the branch to the central repository (`git push origin a-new-dev`)
6. Open a [Pull Request](https://github.com/atolcd/pentaho-doc-plugins/pulls)
7. Wait for the PR to be supported

## LICENSE

This extension is licensed under `GNU Library or "Lesser" General Public License (LGPL)`.

Developed by [Cédric Darbon](https://twitter.com/cedricdarbon) and Roger AIRES.
Packaged by [Charles-Henry Vagner](https://github.com/cvagner)

## Our company

[Atol Conseils et Développements](http://www.atolcd.com)
Follow us on twitter [@atolcd](https://twitter.com/atolcd)
