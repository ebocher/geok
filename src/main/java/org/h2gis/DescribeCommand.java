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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command to describe an input file
 * @author Erwan Bocher
 */
@Command(name = "describe", header = "Describe the source file")
public class DescribeCommand implements Runnable, H2GISConnection{  
   
    private Connection con;
    
    @Option(names = {"-sourceFile", "-sourcefile"}, description = "Path to input file.")
    File inputFile;       
 
    @Override
    public void run() {
        System.out.println("The file is  " + inputFile+ " and the connection is "+ con);
    }

    @Override
    public void setConnection(Connection con) {
        this.con=con;
    }
    
}
