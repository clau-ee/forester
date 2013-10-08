// $Id:
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
// All rights reserved
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
//
// Contact: phylosoft @ gmail . com
// WWW: www.phylosoft.org
// javac -cp ~/SOFTWARE_DEV/ECLIPSE_WORKSPACE/forester/java/forester.jar
// ~/SOFTWARE_DEV/ECLIPSE_WORKSPACE/forester_applications/src/org/forester/applications/simple_node_processor.java
// java -Xmx2048m -cp
// /home/czmasek/SOFTWARE_DEV/ECLIPSE_WORKSPACE/forester_applications/src/:/home/czmasek/SOFTWARE_DEV/ECLIPSE_WORKSPACE/forester/java/forester.jar
// org.forester.applications.simple_node_processor

package org.forester.applications;

import java.io.File;

import org.forester.io.parsers.phyloxml.PhyloXmlDataFormatException;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Identifier;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.phylogeny.factories.ParserBasedPhylogenyFactory;
import org.forester.phylogeny.factories.PhylogenyFactory;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.forester.util.CommandLineArguments;
import org.forester.util.ForesterUtil;

public class tax_code_cleaner {

    private final static String BASE = "b_";

    public static void main( final String args[] ) {
        File in = null;
        File out = null;
        try {
            CommandLineArguments cla = null;
            cla = new CommandLineArguments( args );
            in = cla.getFile( 0 );
            out = cla.getFile( 1 );
            // if ( out.exists() ) {
            //      System.out.println( out + " already exists" );
            //      System.exit( -1 );
            //  }
            final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
            final PhyloXmlParser xml_parser = new PhyloXmlParser();
            final Phylogeny[] phylogenies_0 = factory.create( in, xml_parser );
            final Phylogeny phylogeny_0 = phylogenies_0[ 0 ];
            final PhylogenyNodeIterator it = phylogeny_0.iteratorPostorder();
            int i = 0;
            while ( it.hasNext() ) {
                final PhylogenyNode node = it.next();
                processNode( node, i );
                i++;
            }
            final PhylogenyWriter writer = new PhylogenyWriter();
            writer.toPhyloXML( out, phylogeny_0, 0 );
        }
        catch ( final Exception e ) {
            System.out.println( e.getLocalizedMessage() );
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    private static void processNode( final PhylogenyNode node, final int i ) throws PhyloXmlDataFormatException {
        if ( node.isExternal() ) {
            if ( node.getNodeData().isHasTaxonomy() ) {
                final Taxonomy t = node.getNodeData().getTaxonomy();
                if ( !ForesterUtil.isEmpty( t.getTaxonomyCode() ) ) {
                    final String tc = t.getTaxonomyCode();
                    if ( tc.equals( "ACRALC" ) ) {
                        t.setScientificName( "Acremonium alcalophilum" );
                        t.setTaxonomyCode( "AALXX" );
                    }
                    else if ( tc.equals( "AMPQU" ) ) {
                        t.setScientificName( "Amphimedon queenslandica" );
                        t.setTaxonomyCode( "AMPQE" );
                    }
                    else if ( tc.equals( "AQUAE" ) ) {
                        t.setScientificName( "Aquifex aeolicus (strain VF5)" );
                    }
                    else if ( tc.equals( "ASTSPC" ) ) {
                        t.setScientificName( "Asterochloris sp. Cgr/DA1pho" );
                        t.setTaxonomyCode( "ASCXX" );
                    }
                    else if ( tc.equals( "BAUCOM" ) ) {
                        t.setScientificName( "Baudoinia compniacensis" );
                        t.setTaxonomyCode( "BCOXX" );
                    }
                    else if ( tc.equals( "CAP" ) ) {
                        t.setScientificName( "Capitella sp.1" );
                        t.setTaxonomyCode( "CTEXX" );
                    }
                    else if ( tc.equals( "CAPOWC" ) ) {
                        t.setScientificName( "Capsaspora owczarzaki (strain ATCC 30864)" );
                        t.setTaxonomyCode( "CAPO3" );
                    }
                    else if ( tc.equals( "CHLVUL" ) ) {
                        t.setScientificName( "Chlorella variabilis" );
                        t.setTaxonomyCode( "CHLVA" );
                    }
                    else if ( tc.equals( "CITCLE" ) ) {
                        t.setScientificName( "Citrus clementina" );
                        t.setTaxonomyCode( "CCLXX" );
                    }
                    else if ( tc.equals( "CLAGRA" ) ) {
                        t.setScientificName( "Cladonia grayi" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "COEREV" ) ) {
                        t.setScientificName( "Coemansia reversa" );
                        t.setTaxonomyCode( "CREXX" );
                    }
                    else if ( tc.equals( "CONPUT" ) ) {
                        t.setScientificName( "Coniophora puteana" );
                        t.setTaxonomyCode( "CPUXX" );
                    }
                    else if ( tc.equals( "DICSQU" ) ) {
                        t.setScientificName( "Dichomitus squalens" );
                        t.setTaxonomyCode( "DICSQ" );
                    }
                    else if ( tc.equals( "FOMPIN" ) ) {
                        t.setScientificName( "Fomitopsis pinicola" );
                        t.setTaxonomyCode( "FPIXX" );
                    }
                    else if ( tc.equals( "GONPRO" ) ) {
                        t.setScientificName( "Gonapodya prolifera" );
                        t.setTaxonomyCode( "GONPR" );
                    }
                    else if ( tc.equals( "GYMLUX" ) ) {
                        t.setScientificName( "Gymnopus luxurians" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "HYDPIN" ) ) {
                        t.setScientificName( "Hydnomerulius pinastri" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "JAAARG" ) ) {
                        t.setScientificName( "Jaapia argillacea" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "MYCPOP" ) ) {
                        t.setScientificName( "Mycosphaerella populorum" );
                        t.setTaxonomyCode( "MYCPS" );
                    }
                    else if ( tc.equals( "MYCTHE" ) ) {
                        t.setScientificName( "Myceliophthora thermophila" );
                        t.setTaxonomyCode( "THIHA" );
                    }
                    else if ( tc.equals( "OIDMAI" ) ) {
                        t.setScientificName( "Oidiodendron maius" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "PANVIR" ) ) {
                        t.setScientificName( "Panicum virgatum" );
                        t.setTaxonomyCode( "PANVG" );
                    }
                    else if ( tc.equals( "PIRSPE" ) ) {
                        t.setScientificName( "Piromyces sp. E2" );
                        t.setTaxonomyCode( "PIRSE" );
                    }
                    else if ( tc.equals( "SAICOM" ) ) {
                        t.setScientificName( "Saitoella complicata" );
                        t.setTaxonomyCode( "" );
                    }
                    else if ( tc.equals( "SERLAC" ) ) {
                        t.setScientificName( "Serpula lacrymans" );
                        t.setTaxonomyCode( "SERL9" );
                    }
                    else if ( tc.equals( "SPHARC" ) ) {
                        t.setScientificName( "Sphaeroforma arctica" );
                        t.setTaxonomyCode( "SARXX" );
                    }
                    else if ( tc.equals( "THETRA" ) ) {
                        t.setScientificName( "Thecamonas trahens" );
                        t.setTaxonomyCode( "TTRXX" );
                    }
                    else if ( tc.equals( "THITER" ) ) {
                        t.setScientificName( "Thielavia terrestris (strain ATCC 38088 / NRRL 8126)" );
                        t.setTaxonomyCode( "THITE" );
                    }
                    else if ( tc.equals( "WOLCOC" ) ) {
                        t.setScientificName( "Wolfiporia cocos MD-104 SS10" );
                        t.setTaxonomyCode( "WOLCO" );
                    }
                    else if ( tc.equals( "XANPAR" ) ) {
                        t.setScientificName( "Xanthoria parietina 46-1" );
                        t.setTaxonomyCode( "" );
                    }
                }
            }
        }
    }
}