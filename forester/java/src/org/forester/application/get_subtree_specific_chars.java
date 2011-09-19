// $Id:
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2011 Christian M. Zmasek
// Copyright (C) 2008-2011 Burnham Institute for Medical Research
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
// WWW: www.phylosoft.org/forester

package org.forester.application;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.factories.ParserBasedPhylogenyFactory;
import org.forester.phylogeny.factories.PhylogenyFactory;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.forester.util.ForesterUtil;

public class get_subtree_specific_chars {

    public static void main( final String args[] ) {
        if ( args.length != 1 ) {
            System.err.println();
            System.err.println( "get_subtree_specific_chars: wrong number of arguments" );
            System.err.println( "Usage: \"get_subtree_specific_chars <intree>" );
            System.err.println();
            System.exit( -1 );
        }
        final File infile = new File( args[ 0 ] );
        Phylogeny phy = null;
        try {
            final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
            phy = factory.create( infile, ForesterUtil.createParserDependingOnFileType( infile, true ) )[ 0 ];
        }
        catch ( final Exception e ) {
            System.err.println( e + "\nCould not read " + infile + "\n" );
            System.exit( -1 );
        }
        final SortedSet<Integer> all_external_ids = getAllExternalDescendantsNodeIds( phy.getRoot() );
        final SortedSet<String> all_chars = getAllExternalPresentAndGainedCharacters( phy.getRoot() );
        System.out.println( "Sum of all external characters:\t" + all_chars.size() );
        System.out.println();
        final boolean SIMPLE = false;
        for( final PhylogenyNodeIterator iter = phy.iteratorPostorder(); iter.hasNext(); ) {
            final PhylogenyNode node = iter.next();
            if ( !SIMPLE && node.isExternal() ) {
                continue;
            }
            if ( !node.isRoot() ) {
                System.out.println();
                if ( node.getNodeData().isHasTaxonomy()
                        && !ForesterUtil.isEmpty( node.getNodeData().getTaxonomy().getScientificName() ) ) {
                    System.out.print( node.getName() + " " + node.getNodeData().getTaxonomy().getScientificName() );
                }
                else {
                    System.out.print( node.getName() );
                }
                System.out.println( ":" );
                final SortedSet<Integer> external_ids = getAllExternalDescendantsNodeIds( node );
                final SortedSet<Integer> not_external_ids = copy( all_external_ids );
                not_external_ids.removeAll( external_ids );
                final SortedSet<String> not_node_chars = new TreeSet<String>();
                for( final Integer id : not_external_ids ) {
                    not_node_chars.addAll( getAllExternalPresentAndGainedCharacters( phy.getNode( id ) ) );
                }
                final SortedSet<String> node_chars = getAllExternalPresentAndGainedCharacters( node );
                final SortedSet<String> unique_chars = new TreeSet<String>();
                for( final String node_char : node_chars ) {
                    if ( !not_node_chars.contains( node_char ) ) {
                        if ( SIMPLE ) {
                            unique_chars.add( node_char );
                        }
                        else {
                            boolean found = true;
                            for( final int external_id : external_ids ) {
                                if ( !phy.getNode( external_id ).getNodeData().getBinaryCharacters()
                                        .getGainedCharacters().contains( node_char )
                                        && !phy.getNode( external_id ).getNodeData().getBinaryCharacters()
                                                .getPresentCharacters().contains( node_char ) ) {
                                    found = false;
                                    break;
                                }
                            }
                            if ( found ) {
                                unique_chars.add( node_char );
                            }
                        }
                    }
                }
                System.out.println( "\tSUM:\t" + unique_chars.size() );
                int counter = 1;
                for( final String unique_char : unique_chars ) {
                    System.out.println( "\t" + counter + ":\t" + unique_char );
                    ++counter;
                }
            }
        }
    }

    private static SortedSet<Integer> copy( final SortedSet<Integer> set ) {
        final SortedSet<Integer> copy = new TreeSet<Integer>();
        for( final Integer i : set ) {
            copy.add( i );
        }
        return copy;
    }

    private static SortedSet<Integer> getAllExternalDescendantsNodeIds( final PhylogenyNode node ) {
        final SortedSet<Integer> ids = new TreeSet<Integer>();
        final List<PhylogenyNode> descs = node.getAllExternalDescendants();
        for( final PhylogenyNode desc : descs ) {
            ids.add( desc.getId() );
        }
        return ids;
    }

    private static SortedSet<String> getAllExternalPresentAndGainedCharacters( final PhylogenyNode node ) {
        final SortedSet<String> chars = new TreeSet<String>();
        final List<PhylogenyNode> descs = node.getAllExternalDescendants();
        for( final PhylogenyNode desc : descs ) {
            chars.addAll( desc.getNodeData().getBinaryCharacters().getGainedCharacters() );
            chars.addAll( desc.getNodeData().getBinaryCharacters().getPresentCharacters() );
        }
        return chars;
    }
}