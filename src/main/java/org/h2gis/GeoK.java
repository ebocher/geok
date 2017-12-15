/*
 * GeoK is a command line tool that permits to update, select, transform geospatial
 * data. GeoK is based on the H2GIS library <http://www.h2gis.org>. 
 * 
 * GeoK is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * Copyright (C) 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <https://github.com/orbisgis/geok/>
 * or contact directly: info_at_h2gis.org
 */
package org.h2gis;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.h2gis.functions.factory.H2GISFunctions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

/**
 * GeoK is a geospatial knife based on H2GIS database. GeoK
 * could be used to transform, select, update data from files or databases.
 * 
 * GeoK uses a temporay H2GIS spatial database and takes profit of its 
 * capabilities to process data.
 *
 * @author Erwan Bocher
 */
@Command(name = "GeoK", header = "Welcome to the geospatial breizh knife!")
public class GeoK implements Runnable {
    
    String currentVersion =  "0.0.1";

    @Option(names = {"-version", "-v"}, usageHelp = true, description = "Return the current version of GeoK.")
    boolean version;

    @Override
    public void run() {
        if (version) {
            System.out.println("GeoK version is " + currentVersion);    
            System.out.println("JVM Vendor : "+ System.getProperty("java.vendor")+ " Version : "+ System.getProperty("java.version"));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new GeoK());
        commandLine.addSubcommand("describe", new DescribeCommand());
        commandLine.addSubcommand("script", new ScriptCommand());
        try {
        List<CommandLine> parsed = commandLine.parse(args);
        Connection con = null;
        for (CommandLine command : parsed) {
            Object commandObject = command.getCommand();
            if (commandObject.getClass() == DescribeCommand.class) {
                DescribeCommand describeCommand = (DescribeCommand) commandObject;
                con = createDatabase();
                describeCommand.setConnection(con);
                describeCommand.run();
            } else if (commandObject.getClass() == GeoK.class) {
                GeoK h2GISKontell = (GeoK) commandObject;
                h2GISKontell.run();
            } else if (commandObject.getClass() == ScriptCommand.class) {
                ScriptCommand scriptCommand = (ScriptCommand) commandObject;
                con = createDatabase();
                scriptCommand.setConnection(con);
                scriptCommand.run();
            }
        }
        
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            System.out.println("Cannot close the database.");
        }
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            ex.getCommandLine().usage(System.err);
        } catch (Exception ex) {
            throw new ExecutionException(commandLine, "Error while calling ", ex);
        }
    }

    /**
     * Create a local temporary database that will be used to run the commands
     *
     * @return TODO : use a default file to configure the database
     */
    private static Connection createDatabase() {
        String H2_PARAMETERS = ";LOCK_MODE=0;LOG=0";
        String dbFilePath = System.getProperty("java.io.tmpdir") + File.separator + "geok_" + System.currentTimeMillis();
        File dbFile = new File(dbFilePath + ".mv.db");
        String databasePath = "jdbc:h2:" + dbFilePath + H2_PARAMETERS;
        if (dbFile.exists()) {
            dbFile.delete();
        }
        dbFile = new File(dbFilePath + ".mv.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        org.h2.Driver.load();
        // Keep a connection alive to not close the DataBase on each unit test
        Connection connection;
        try {
            connection = DriverManager.getConnection(databasePath,
                    "sa", "sa");
            // Init spatial ext
            H2GISFunctions.load(connection);
            return connection;
        } catch (SQLException ex) {
            System.err.println("Cannot create the local database. " + ex.getMessage());
        }
        return null;
    }

}
