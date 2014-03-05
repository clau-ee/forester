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
// WWW: https://sites.google.com/site/cmzmasek/home/software/forester

package org.forester.evoinference.distance;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.util.ForesterUtil;

public final class NeighborJoining {

    private BasicSymmetricalDistanceMatrix _d;
    private double[][]                     _d_values;
    private final DecimalFormat            _df;
    private PhylogenyNode[]                _external_nodes;
    private double[][]                     _m_values;
    private int[]                          _mappings;
    private int                            _n;
    private double[]                       _r;
    private final boolean                  _verbose;

    private NeighborJoining() {
        _verbose = false;
        _df = null;
    }

    private NeighborJoining( final boolean verbose, final int maximum_fraction_digits_for_distances ) {
        if ( ( maximum_fraction_digits_for_distances < 1 ) || ( maximum_fraction_digits_for_distances > 9 ) ) {
            throw new IllegalArgumentException( "maximum fraction digits for distances is out of range: "
                    + maximum_fraction_digits_for_distances );
        }
        _verbose = verbose;
        _df = new DecimalFormat();
        _df.setMaximumFractionDigits( maximum_fraction_digits_for_distances );
        _df.setRoundingMode( RoundingMode.HALF_UP );
    }

    public final Phylogeny execute( final BasicSymmetricalDistanceMatrix distance ) {
        reset( distance );
        final Phylogeny phylogeny = new Phylogeny();
        while ( _n > 2 ) {
            updateM();
            // Calculates the minimal distance.
            // If more than one minimal distances, always the first found is used
            // could randomize this, so that any would be returned in a randomized fashion...
            double minimum = _m_values[ 0 ][ 1 ];
            int otu1 = 0;
            int otu2 = 1;
            for( int j = 1; j < _n; ++j ) {
                for( int i = 0; i < j; ++i ) {
                    if ( _m_values[ i ][ j ] < minimum ) {
                        minimum = _m_values[ i ][ j ];
                        otu1 = i;
                        otu2 = j;
                    }
                }
            }
            // It is a condition that otu1 < otu2.
            final PhylogenyNode node = new PhylogenyNode();
            final double d = _d_values[ _mappings[ otu1 ] ][ _mappings[ otu2 ] ];
            final double d1 = ( d / 2 ) + ( ( _r[ otu1 ] - _r[ otu2 ] ) / ( 2 * ( _n - 2 ) ) );
            final double d2 = d - d1;
            if ( _df == null ) {
                getExternalPhylogenyNode( otu1 ).setDistanceToParent( d1 );
                getExternalPhylogenyNode( otu2 ).setDistanceToParent( d2 );
            }
            else {
                // yes, yes, slow but only grows with n (and not n^2 or worse)...
                getExternalPhylogenyNode( otu1 ).setDistanceToParent( Double.parseDouble( _df.format( d1 ) ) );
                getExternalPhylogenyNode( otu2 ).setDistanceToParent( Double.parseDouble( _df.format( d2 ) ) );
            }
            node.addAsChild( getExternalPhylogenyNode( otu1 ) );
            node.addAsChild( getExternalPhylogenyNode( otu2 ) );
            if ( _verbose ) {
                printProgress( otu1, otu2 );
            }
            calculateDistancesFromNewNode( otu1, otu2, d );
            _external_nodes[ _mappings[ otu1 ] ] = node;
            updateMappings( otu2 );
            --_n;
        }
        final double d = _d_values[ _mappings[ 0 ] ][ _mappings[ 1 ] ] / 2;
        if ( _df == null ) {
            getExternalPhylogenyNode( 0 ).setDistanceToParent( d );
            getExternalPhylogenyNode( 1 ).setDistanceToParent( d );
        }
        else {
            final double dd = Double.parseDouble( _df.format( d ) );
            getExternalPhylogenyNode( 0 ).setDistanceToParent( dd );
            getExternalPhylogenyNode( 1 ).setDistanceToParent( dd );
        }
        final PhylogenyNode root = new PhylogenyNode();
        root.addAsChild( getExternalPhylogenyNode( 0 ) );
        root.addAsChild( getExternalPhylogenyNode( 1 ) );
        if ( _verbose ) {
            printProgress( 0, 1 );
        }
        phylogeny.setRoot( root );
        phylogeny.setRooted( false );
        return phylogeny;
    }

    public final List<Phylogeny> execute( final List<BasicSymmetricalDistanceMatrix> distances_list ) {
        final List<Phylogeny> pl = new ArrayList<Phylogeny>();
        for( final BasicSymmetricalDistanceMatrix distances : distances_list ) {
            pl.add( execute( distances ) );
        }
        return pl;
    }

    private final void calculateDistancesFromNewNode( final int otu1, final int otu2, final double d ) {
        final int m_otu1 = _mappings[ otu1 ];
        final int m_otu2 = _mappings[ otu2 ];
        for( int i = 0; i < _n; ++i ) {
            if ( ( i == otu1 ) || ( i == otu2 ) ) {
                continue;
            }
            final int m_i = _mappings[ i ];
            if ( otu1 < i ) {
                if ( otu2 > i ) {
                    _d_values[ m_otu1 ][ m_i ] = ( _d_values[ m_otu1 ][ m_i ] + _d_values[ m_i ][ m_otu2 ] - d ) / 2;
                }
                else {
                    _d_values[ m_otu1 ][ m_i ] = ( _d_values[ m_otu1 ][ m_i ] + _d_values[ m_otu2 ][ m_i ] - d ) / 2;
                }
            }
            else {
                if ( otu2 > i ) {
                    _d_values[ m_i ][ m_otu1 ] = ( _d_values[ m_i ][ m_otu1 ] + _d_values[ m_i ][ m_otu2 ] - d ) / 2;
                }
                else {
                    _d_values[ m_i ][ m_otu1 ] = ( _d_values[ m_i ][ m_otu1 ] + _d_values[ m_otu2 ][ m_i ] - d ) / 2;
                }
            }
        }
    }

    private final void calculateNetDivergences() {
        double d;
        for( int i = 0; i < _n; ++i ) {
            d = 0;
            final int m_i = _mappings[ i ];
            for( int n = 0; n < _n; ++n ) {
                if ( i != n ) {
                    if ( i > n ) {
                        d += _d_values[ _mappings[ n ] ][ m_i ];
                    }
                    else {
                        d += _d_values[ m_i ][ _mappings[ n ] ];
                    }
                }
            }
            _r[ i ] = d;
        }
    }

    private final PhylogenyNode getExternalPhylogenyNode( final int i ) {
        return _external_nodes[ _mappings[ i ] ];
    }

    private final void initExternalNodes() {
        _external_nodes = new PhylogenyNode[ _n ];
        String id;
        for( int i = 0; i < _n; ++i ) {
            _external_nodes[ i ] = new PhylogenyNode();
            id = _d.getIdentifier( i );
            if ( id != null ) {
                _external_nodes[ i ].setName( id );
            }
            else {
                _external_nodes[ i ].setName( Integer.toString( i ) );
            }
            _mappings[ i ] = i;
        }
    }

    private final void printD() {
        System.out.println( "D:" );
        for( final double[] _d_value : _d_values ) {
            for( int j = 0; j < _d_values.length; j++ ) {
                System.out.print( _d_value[ j ] );
                System.out.print( " " );
            }
            System.out.println();
        }
        System.out.println();
    }

    private final void printM() {
        System.out.println( "M:" );
        for( final double[] _m_value : _m_values ) {
            for( int j = 0; j < _m_values.length; j++ ) {
                System.out.print( _m_value[ j ] );
                System.out.print( " " );
            }
            System.out.println();
        }
        System.out.println();
    }

    private final void printProgress( final int otu1, final int otu2 ) {
        final PhylogenyNode n1 = getExternalPhylogenyNode( otu1 );
        final PhylogenyNode n2 = getExternalPhylogenyNode( otu2 );
        System.out.println( "Node " + ( ForesterUtil.isEmpty( n1.getName() ) ? n1.getId() : n1.getName() ) + " joins "
                + ( ForesterUtil.isEmpty( n2.getName() ) ? n2.getId() : n2.getName() ) );
    }

    // only the values in the lower triangle are used.
    // !matrix values will be changed!
    private final void reset( final BasicSymmetricalDistanceMatrix distances ) {
        _n = distances.getSize();
        _d = distances;
        _r = new double[ _n ];
        _mappings = new int[ _n ];
        _d_values = _d.getValues();
        _m_values = new double[ _n ][ _n ];
        initExternalNodes();
    }

    private final void updateM() {
        calculateNetDivergences();
        final int n_minus_2 = _n - 2;
        for( int j = 1; j < _n; ++j ) {
            final double r_j = _r[ j ];
            final int m_j = _mappings[ j ];
            for( int i = 0; i < j; ++i ) {
                _m_values[ i ][ j ] = _d_values[ _mappings[ i ] ][ m_j ] - ( ( _r[ i ] + r_j ) / n_minus_2 );
            }
        }
    }

    // otu2 will, in effect, be "deleted" from the matrix.
    private final void updateMappings( final int otu2 ) {
        for( int i = otu2; i < ( _mappings.length - 1 ); ++i ) {
            _mappings[ i ] = _mappings[ i + 1 ];
        }
    }

    public final static NeighborJoining createInstance() {
        return new NeighborJoining();
    }

    public final static NeighborJoining createInstance( final boolean verbose,
                                                        final int maximum_fraction_digits_for_distances ) {
        return new NeighborJoining( verbose, maximum_fraction_digits_for_distances );
    }
}
