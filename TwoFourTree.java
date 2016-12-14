package javatermproject;

/**
 * Title: Term Project 2-4 Trees Description: Copyright: Copyright (c) 2001
 * Company: Brennon Gee, Jacob Van Veldhuzien Date last modified: 5 Dec 2016
 * Changelog: 5.12.16: Changed formatting, added @Override tags, basic
 * commenting - Jacob 7.12.16: Changed format so G's code no longer violates CS
 * style guide, edited InsertItem slightly, added FFGE() (will need to update
 * soon), added Javadoc support
 *
 * @author
 * @version 1.0
 */
public class TwoFourTree implements Dictionary
{
    //Class vars
    private Comparator treeComp;
    private int size = 0;                           //Remember to in/decrement where necessary
    private TFNode treeRoot = null;

    /**
     * Constructors
     *
     * @param comp
     */
    public TwoFourTree( Comparator comp )
    {
        treeComp = comp;
    }

    private TFNode root()
    {
        return treeRoot;
    }

    private void setRoot( TFNode root )
    {
        treeRoot = root;
    }

    /**
     * Size functions
     *
     * @return
     */
    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return ( size == 0 );
    }

    /**
     * Searches dictionary to determine if key is present
     *
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    @Override
    public Object findElement( Object key )
    {
        Item itm = new Item( key, 0 );
        return FFGE( itm, treeRoot );
    }

    /**
     * Inserts provided element into the Dictionary
     *
     * @param key of object to be inserted
     * @param element to be inserted
     */
    @Override
    public void insertElement( Object key, Object element ) throws TFNodeException
    {
        //If bad val given
        if ( !treeComp.isComparable( key ) || !treeComp.isComparable( element ) )
        {
            throw new TFNodeException( "Only integers may be inserted into the tree" );
        }
        //Otherwise
        else
        {
            Item itm = new Item( key, element );
            TFNode pos;

            if ( treeRoot == null )  //First insert
            {
                treeRoot = new TFNode();
                treeRoot.addItem( 0, itm );
                ++size;
            }
            else
            {
                pos = searchTree( itm, treeRoot );
                int index = FFGE( itm, pos );
                pos.insertItem( index, itm );
                ++size;

                //Check for/handle overflow
                handleOverflow( pos );
            }
        }
    }

    /**
     * Handles overflow of TFN values
     *
     * @param pos
     */
    public void handleOverflow( TFNode pos )
    {
        Item itm;
        Item itm2;
        TFNode nN = new TFNode();
        TFNode par;
        int index;

        if ( pos.getNumItems() == 4 )
        {
            if ( pos == treeRoot )  //If root overflows
            {
                treeRoot = new TFNode();
                treeRoot.setChild( 0, pos );
                pos.setParent( treeRoot );
            }

            itm2 = pos.deleteItem( 3 );
            itm = pos.deleteItem( 2 );
            par = pos.getParent();  //null if treeRoot
            index = FFGE( itm, par );
            nN.addItem( 0, itm2 );
            par.insertItem( index, itm );
            //Parent/Child handling
            nN.setParent( par );
            par.setChild( index + 1, nN );
            //Child/Child handling
            if ( pos.getChild( 3 ) != null )  //If it's null, no harm no foul
            {
                TFNode pC3 = pos.getChild( 3 );
                TFNode pC4 = pos.getChild( 4 );
                nN.setChild( 0, pC3 );
                nN.setChild( 1, pC4 );
                pC3.setParent( nN );
                pC4.setParent( nN );
            }
        }

        par = pos.getParent();
        if ( par != null )
        {
            handleOverflow( pos.getParent() );
        }
    }

    /**
     * Handles potential underflow of pos
     *
     * @param pos
     */
    public void handleUnderflow( TFNode pos, int index )
    {
        TFNode tracer = pos;
        
        if ( checkLeftChild( tracer, index ) )
        {
            //Pull biggest from left
            leftTransfer( tracer, index );
        }
        else if ( checkRightChild( tracer, index ) )
        {
            //Pull smallest from right (removeItem)
            rightTransfer( tracer, index );
        }
        else if ( WCAI( tracer.getChild( index ) ) == 0 || WCAI( tracer.getChild( index ) ) == 1 )
        {
            //Left fusion
            leftFusion( tracer, index );
            handleUnderflow( tracer, index );
            if( pos == treeRoot )
            {
                tracer = pos.getChild( 0 );
                tracer = treeRoot;
                tracer.setParent( null );
            }
        }
        else
        {
            //Right fusion
            rightFusion( tracer, index );
            handleUnderflow( tracer, index );
        }
    }

    public int WCAI( TFNode child )
    {
        TFNode par = child.getParent();
        int childNum = -1;

        for ( int k = 0; k < 5; ++k )
        {
            if ( child == par.getChild( k ) )
            {
                childNum = k;
            }
        }

        return childNum;
    }

    public boolean checkLeftChild( TFNode pos, int index )
    {
        TFNode tracer = pos.getChild( index );

        if ( tracer == null )
        {
            return false;
        }
        else if ( tracer.getNumItems() <= 1 )
        {
            return false;
        }

        return true;
    }

    public boolean checkRightChild( TFNode pos, int index )
    {
        TFNode tracer = pos.getChild( index + 1 );

        if ( tracer == null )
        {
            return false;
        }
        else if ( tracer.getNumItems() <= 1 )
        {
            return false;
        }

        return true;
    }

    public void leftTransfer( TFNode pos, int index )
    {
        TFNode tracer = pos.getChild( index );
        Item itm = tracer.removeItem( tracer.getNumItems() - 1 );
        tracer.addItem( index, itm );
    }

    public void rightTransfer( TFNode pos, int index )
    {
        TFNode tracer = pos.getChild( index + 1 );
        Item itm = tracer.removeItem( index );
        tracer.addItem( index, itm );
    }

    public void leftFusion( TFNode pos, int index )
    {
        TFNode lChild = pos.getChild( index );
        TFNode rChild = pos.getChild( index + 1 );
        Item itm = rChild.deleteItem( 0 );
        pos.addItem( index, itm );
        pos.setChild( 1, lChild );
        rChild.setParent( null );
        lChild.addItem( 1, pos.removeItem( index ) );
    }

    public void rightFusion( TFNode pos, int index )
    {
        TFNode lChild = pos.getChild( index );
        TFNode rChild = pos.getChild( index + 1 );
        Item itm = lChild.deleteItem( lChild.getNumItems() - 1 );
        pos.addItem( index, itm );
        pos.setChild( 0, rChild );
        lChild.setParent( null );
        rChild.addItem( 1, pos.removeItem( index ) );
    }

    /**
     * Finds and returns the first TFNode at which a new Item can be inserted
     * into the tree
     *
     * @param itm
     * @param tfn
     * @return
     */
    public TFNode searchTree( Item itm, TFNode tfn )
    {
        int ffgeRslt;
        TFNode pos = tfn;

        ffgeRslt = FFGE( itm, pos );

        if ( 0 <= ffgeRslt && ffgeRslt <= pos.getNumItems() )
        {
            if ( pos.getChild( ffgeRslt ) != null )
            {
                pos = pos.getChild( ffgeRslt );
                searchTree( itm, pos );
            }
        }
        else
        {
            pos = null;  //Should never happen
        }

        return pos;  //CATCHALL
    }

    /**
     * Finds and returns the index of the first Item in a node with a value
     * greater than or equal to the Item given. If this function returns
     * pos.getNumItems(), there was no Item in the node with a key value greater
     * than or equal to that of the item passed.
     *
     * @param itm
     * @param tfn
     * @return
     */
    public int FFGE( Item itm, TFNode tfn ) throws TFNodeException
    {
        if ( tfn == null )
        {
            throw new TFNodeException( "TFNode passed is null" );
        }

        TFNode pos = tfn;
        int index = pos.getNumItems();

        for ( int k = 0; k < pos.getNumItems(); ++k )  //Iterate thru TFN's Item array
        {
            if ( treeComp.isLessThanOrEqualTo( itm.key(), pos.getItem( k ).key() ) )  //If an Item's key in pos >= itm's key
            {
                index = k;
                break;    //If fG, break out otherwise we keep setting index
            }
        }

        return index;
    }

    /**
     * Searches dictionary to determine if key is present, then removes and
     * returns corresponding object
     *
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    @Override
    public Object removeElement( Object key ) throws ElementNotFoundException
    {
        TFNode pos;
        Item itm = new Item( key, 0 );
        Item ret;
        int index;

        if ( !treeComp.isComparable( key ) )
        {
            throw new ElementNotFoundException( "Key passed is invalid and could not be located" );
        }
        if ( this.isEmpty() )
        {
            throw new ElementNotFoundException( "There are no elements to remove" );
        }

        pos = searchTree( itm, treeRoot );
        index = FFGE( itm, pos );
        ret = pos.deleteItem( index );
        handleUnderflow( pos, index );

        return ret.element();  //DO nullchk
    }

    /**
     * This prints all elements and their indices (?) throughout the tree
     */
    public void printAllElements()
    {
        int indent = 0;
        if ( root() == null )
        {
            System.out.println( "The tree is empty" );
        }
        else
        {
            printTree( root(), indent );
        }
    }

    /**
     * This will print out all elements in the tree; subroutine of
     * printAllElements()
     *
     * @param start
     * @param indent
     */
    public void printTree( TFNode start, int indent )
    {
        if ( start == null )
        {
            return;
        }
        for ( int i = 0; i < indent; ++i )
        {
            System.out.print( " " );
        }
        printTFNode( start );
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for ( int i = 0; i < numChildren; ++i )
        {
            printTree( start.getChild( i ), indent );
        }
    }

    /**
     * This prints out the individual values inside of the TFNodes in the tree
     *
     * @param node
     */
    public void printTFNode( TFNode node )
    {
        int numItems = node.getNumItems();
        for ( int i = 0; i < numItems; ++i )
        {
            System.out.print( ( ( Item ) node.getItem( i ) ).element() + " " );
        }
        System.out.println();
    }

    /**
     * This performs checkTreeFromNode on treeRoot. Checks if tree is properly
     * hooked up, i.e., children point to parents
     */
    public void checkTree()
    {
        checkTreeFromNode( treeRoot );
    }

    /**
     * Actually walks through the tree from the given TFNode to determine
     * children
     *
     * @param start
     */
    private void checkTreeFromNode( TFNode start )
    {
        if ( start == null )
        {
            return;
        }

        if ( start.getParent() != null )
        {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for ( childIndex = 0; childIndex <= parent.getNumItems(); childIndex++ )
            {
                if ( parent.getChild( childIndex ) == start )
                {
                    break;
                }
            }
            // if child wasn't found, print problem
            if ( childIndex > parent.getNumItems() )
            {
                System.out.println( "Child to parent confusion" );
                printTFNode( start );
            }
        }

        if ( start.getChild( 0 ) != null )
        {
            for ( int childIndex = 0; childIndex <= start.getNumItems(); childIndex++ )
            {
                if ( start.getChild( childIndex ) == null )
                {
                    System.out.println( "Mixed null and non-null children" );
                    printTFNode( start );
                }
                else
                {
                    if ( start.getChild( childIndex ).getParent() != start )
                    {
                        System.out.println( "Parent to child confusion" );
                        printTFNode( start );
                    }
                    for ( int i = childIndex - 1; i >= 0; i-- )   //Exception for bug found here
                    {
                        if ( start.getChild( i ) == start.getChild( childIndex ) )
                        {
                            System.out.println( "Duplicate children of node" );
                            printTFNode( start );
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for ( int childIndex = 0; childIndex < numChildren; childIndex++ )
        {
            checkTreeFromNode( start.getChild( childIndex ) );
        }

    }

    public static void main( String[] args )
    {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree( myComp );

        Integer myInt1 = new Integer( 47 );
        myTree.insertElement( myInt1, myInt1 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt2 = new Integer( 83 );
        myTree.insertElement( myInt2, myInt2 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt3 = new Integer( 22 );
        myTree.insertElement( myInt3, myInt3 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt4 = new Integer( 16 );
        myTree.insertElement( myInt4, myInt4 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt5 = new Integer( 49 );
        myTree.insertElement( myInt5, myInt5 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt6 = new Integer( 100 );
        myTree.insertElement( myInt6, myInt6 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt7 = new Integer( 38 );
        myTree.insertElement( myInt7, myInt7 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt8 = new Integer( 3 );
        myTree.insertElement( myInt8, myInt8 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt9 = new Integer( 53 );
        myTree.insertElement( myInt9, myInt9 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt10 = new Integer( 66 );
        myTree.insertElement( myInt10, myInt10 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt11 = new Integer( 19 );
        myTree.insertElement( myInt11, myInt11 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt12 = new Integer( 23 );
        myTree.insertElement( myInt12, myInt12 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt13 = new Integer( 24 );
        myTree.insertElement( myInt13, myInt13 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt14 = new Integer( 88 );
        myTree.insertElement( myInt14, myInt14 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt15 = new Integer( 1 );
        myTree.insertElement( myInt15, myInt15 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt16 = new Integer( 97 );
        myTree.insertElement( myInt16, myInt16 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt17 = new Integer( 94 );
        myTree.insertElement( myInt17, myInt17 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt18 = new Integer( 35 );
        myTree.insertElement( myInt18, myInt18 );
        myTree.checkTree();
        myTree.printAllElements();
        Integer myInt19 = new Integer( 51 );
        myTree.insertElement( myInt19, myInt19 );

        myTree.printAllElements();

        System.out.println( "done" );

        myTree = new TwoFourTree( myComp );
        final int TEST_SIZE = 10000;

        for ( int i = 0; i < TEST_SIZE; ++i )  //Insert bugging SPECIFICALLY HERE
        {
            myTree.insertElement( new Integer( i ), new Integer( i ) );
            //          myTree.printAllElements();
            //         myTree.checkTree();
        }
        
        System.out.println( "removing" );
        
        for ( int i = 0; i < TEST_SIZE; ++i )
        {
            int out = ( Integer ) myTree.removeElement( new Integer( i ) );
            if ( out != i )
            {
                throw new TwoFourTreeException( "main: wrong element removed" );
            }
            if ( i > TEST_SIZE - 15 )
            {
                myTree.printAllElements();
            }
        }
        System.out.println( "done" );
    }
}
