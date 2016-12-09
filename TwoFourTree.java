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
    public Object findElement( Object key )   //KWF: This needs to be implemented first
    {
        throw new UnsupportedOperationException( "Not supported yet" );
    }

    /**
     * Inserts provided element into the Dictionary
     *
     * @param key of object to be inserted
     * @param element to be inserted
     */
    @Override
    public void insertElement( Object key, Object element ) throws TFNodeException  //INDEV KWF HOOK UP KIDS
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
            TFNode pos = null;  //Debugging KWF

            if ( treeRoot == null )
            {
                treeRoot = new TFNode();
                treeRoot.addItem( 0, itm );
            }
            else
            {
                pos = searchTree( itm );

                if ( pos.getNumItems() == 0 )
                {
                    pos.addItem( 0, itm );
                }
                else
                {
                    int index;
                    for ( int k = 0; k < pos.getNumItems(); ++k )
                    {
                        index = k;
                        if ( treeComp.isLessThanOrEqualTo( itm.key(), pos.getItem( k ).key() ) )
                        {
                            pos.insertItem( index, itm );
                            k = pos.getNumItems();  //Killcon
                        }
                        else if ( k == pos.getNumItems() - 1 )
                        {
                            pos.addItem( pos.getNumItems(), itm );
                        }
                    }
                }

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
        Item item2 = new Item();
        TFNode nN = new TFNode();
        TFNode par = pos.getParent();
        int index;

        if ( pos.getNumItems() == 4 )
        {
            if ( pos == treeRoot )
            {
                treeRoot = new TFNode();
                treeRoot.setChild( 0, pos );
                pos.setParent( treeRoot );
            }

            item2 = pos.removeItem( 2 );
            nN.addItem( 0, pos.getItem( 2 ) );  //On the "new" 2
        }

        if ( par != null )
        {
            for ( int k = 0; k < par.getNumItems(); ++k )
            {
                if ( treeComp.isLessThanOrEqualTo( item2.element(), par.getItem( k ) ) )
                {
                    index = k;
                    par.insertItem( index, item2 );
                }
            }

            handleOverflow( par );
        }
    }

    /**
     * Finds and returns the first TFNode at which a new Item can be inserted
     * into the tree
     *
     * @param itm
     * @return
     */
    public TFNode searchTree( Item itm )
    {
        int ffgeRslt;
        TFNode pos = treeRoot;

        ffgeRslt = FFGE( itm, pos );

        if ( ffgeRslt == -1 )   //If no greater val found
        {
            if ( pos.getChild( 2 ) != null )  //Check to see if itemArr is full
            {
                pos = pos.getChild( 2 );
            }
            else
            {
                return pos;
            }
        }
        else if ( pos.getChild( ffgeRslt ) != null )
        {
            pos = pos.getChild( ffgeRslt );
        }
        else
        {
            return pos;
        }

        return pos;
    }

    /**
     * Finds and returns the index of the first Item in a node with a value
     * greater than or equal to the Item given. If this function returns a -1,
     * there was no Item in the node with a key value greater than or equal to
     * that of the item passed.
     *
     * @param itm
     * @param tfn
     * @return
     */
    public int FFGE( Item itm, TFNode tfn )
    {
        boolean foundGreater = false;
        TFNode pos = tfn;
        int index = -1;

        while ( !foundGreater )  //While we have found no greater value than that of itm's key
        {
            for ( int k = 0; k < pos.getNumItems(); ++k )  //Iterate thru TFN's Item array
            {
                if ( ( Integer ) pos.getItem( k ).key() >= ( Integer ) itm.key() )  //If an Item's key in pos >= itm's key
                {
                    foundGreater = true;
                    index = k;
                }
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
        throw new UnsupportedOperationException( "Remove not yet supported" );
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
                    for ( int i = childIndex - 1; i >= 0; i-- )
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
        myTree.printAllElements();
        Integer myInt2 = new Integer( 83 );
        myTree.insertElement( myInt2, myInt2 );
        myTree.printAllElements();
        Integer myInt3 = new Integer( 22 );
        myTree.insertElement( myInt3, myInt3 );
        myTree.printAllElements();
        Integer myInt4 = new Integer( 16 );
        myTree.insertElement( myInt4, myInt4 );
        myTree.printAllElements();
        Integer myInt5 = new Integer( 49 );
        myTree.insertElement( myInt5, myInt5 );
        myTree.printAllElements();
        Integer myInt6 = new Integer( 100 );
        myTree.insertElement( myInt6, myInt6 );

        Integer myInt7 = new Integer( 38 );
        myTree.insertElement( myInt7, myInt7 );

        Integer myInt8 = new Integer( 3 );
        myTree.insertElement( myInt8, myInt8 );

        Integer myInt9 = new Integer( 53 );
        myTree.insertElement( myInt9, myInt9 );

        Integer myInt10 = new Integer( 66 );
        myTree.insertElement( myInt10, myInt10 );

        Integer myInt11 = new Integer( 19 );
        myTree.insertElement( myInt11, myInt11 );

        Integer myInt12 = new Integer( 23 );
        myTree.insertElement( myInt12, myInt12 );

        Integer myInt13 = new Integer( 24 );
        myTree.insertElement( myInt13, myInt13 );

        Integer myInt14 = new Integer( 88 );
        myTree.insertElement( myInt14, myInt14 );

        Integer myInt15 = new Integer( 1 );
        myTree.insertElement( myInt15, myInt15 );

        Integer myInt16 = new Integer( 97 );
        myTree.insertElement( myInt16, myInt16 );

        Integer myInt17 = new Integer( 94 );
        myTree.insertElement( myInt17, myInt17 );

        Integer myInt18 = new Integer( 35 );
        myTree.insertElement( myInt18, myInt18 );

        Integer myInt19 = new Integer( 51 );
        myTree.insertElement( myInt19, myInt19 );

        myTree.printAllElements();
        System.out.println( "done" );

        myTree = new TwoFourTree( myComp );
        final int TEST_SIZE = 10000;

        for ( int i = 0; i < TEST_SIZE; ++i )
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
