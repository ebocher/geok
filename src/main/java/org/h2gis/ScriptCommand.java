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

import groovy.lang.GroovyShell;
import groovy.sql.Sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.h2.tools.RunScript;
import picocli.CommandLine;

/**
 *
 * @author Erwan Bocher
 */
@CommandLine.Command(name = "script", header = "Run a script on the default H2GIS database.")
public class ScriptCommand implements Runnable , H2GISConnection {

    private Connection con;       
    
    @CommandLine.Option(names = {"-sql"}, description = "Execute a SQL file.")
    File sqlFile; 
    
    @CommandLine.Option(names = {"-g"}, description = "Execute a Groovy file.")
    File groovyFile; 

    @Override
    public void run() {
        if (sqlFile != null && groovyFile != null) {
            System.out.println("Please use -sql or -g (-groovy)");
        } else if (groovyFile != null) {
            executeGroovy(con, groovyFile);

        } else if (sqlFile != null) {
            executeSQL(con, sqlFile);
        }
 
    }

    @Override
    public void setConnection(Connection con) {
        this.con=con;
    }
    
    /**
     * Execute a sql script file
     * 
     * @param connection
     * @param sqlFile
     * @throws SQLException 
     */
    private void executeSQL(Connection connection, File sqlFile)  {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(sqlFile));
            long startScript = System.currentTimeMillis();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println("SQL script started at "+ dateFormat.format(new Date(startScript)));
            RunScript.execute(connection, reader);
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endSCript = System.currentTimeMillis();
            System.out.println("SQL script finished at "+ dateFormat.format(new Date(endSCript))+"\n It takes "+ ((endSCript-startScript)/1000)+ " s");
   
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Execute a groovy script
     * @param con
     * @param groovyFile 
     */
    private void executeGroovy(Connection con, File groovyFile) {
        try {
            GroovyShell groovyShell = new GroovyShell();
            groovyShell.setProperty("out", System.out);
            groovyShell.setProperty("out", System.err);
            groovyShell.setProperty("sql", new Sql(con));
            long startScript = System.currentTimeMillis();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println("Groovy script started at "+ dateFormat.format(new Date(startScript)));
            groovyShell.evaluate(groovyFile);
            long endSCript = System.currentTimeMillis();
            System.out.println("Groovy script finished at "+ dateFormat.format(new Date(endSCript))+"\n It takes "+ ((endSCript-startScript)/1000)+ " s");
        } catch (Exception e) {
            System.out.println("Cannot execute the Groovy script" + "\n" + e.getLocalizedMessage());
        } finally {
        }

    }
    
}
