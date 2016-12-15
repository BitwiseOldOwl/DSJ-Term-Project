package javatermproject;

import java.util.Random;

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
        TFNode pos = searchTree( itm, treeRoot );
        return FE( itm, pos );
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
                int eq = FE(itm, pos);
                TFNode insertNode = null;
                if (eq == -1 || pos.getChild( 0) == null){
                    pos.insertItem( index, itm );
                    insertNode = pos;
                }
                else {
                    TFNode succ = pos.getChild( index + 1);
                    while (succ.getChild( 0) != null){
                        succ = succ.getChild(0);
                    }
                    succ.insertItem( 0, itm);
                    insertNode = succ;
                }
                
                ++size;

                //Check for/handle overflow
                handleOverflow( insertNode );
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
        TFNode nN = new TFNode();
        TFNode par = null;
        int index;
        int childIndex;
        Item itm3;
        Item itm2;
        TFNode kid3;
        TFNode kid4;

        if ( pos.getNumItems() == 4 )
        {
            if ( pos == treeRoot )  //If root overflows
            {
                treeRoot = new TFNode();
                treeRoot.setChild( 0, pos );
                pos.setParent( treeRoot );
            }

            par = pos.getParent();  //null if treeRoot
            itm2 = pos.getItem( 2 );
            itm3 = pos.getItem( 3 );
            kid3 = pos.getChild( 3 );
            kid4 = pos.getChild( 4 );
            nN.addItem( 0, itm3 );
            nN.setChild( 0, kid3 );
            nN.setChild( 1, kid4 );
            if ( nN.getChild( 0 ) != null )
            {
                nN.getChild( 0 ).setParent( nN );
                nN.getChild( 1 ).setParent( nN );
            }
            pos.deleteItem( 3 );
            pos.deleteItem( 2 );
            childIndex = WCAI( pos );
            par.insertItem( childIndex, itm2 );
            par.setChild( childIndex + 1, nN );
            nN.setParent( par );
        }

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
    public void handleUnderflow( TFNode pos )
    {
        TFNode par = pos.getParent();
        Item search;
        int childIndex = WCAI( pos );

        if ( pos == treeRoot )
        {
            treeRoot = pos.getChild( 0 );
            if ( treeRoot != null )
            {
                treeRoot.setParent( null );
            }
        }
        else if ( childIndex < par.getNumItems() && par.getChild( childIndex + 1 ).getNumItems() > 1 )
        {
            leftTransfer( pos, childIndex );
        }
        else if ( childIndex > 0 && par.getChild( childIndex - 1 ).getNumItems() > 1 )
        {
            rightTransfer( pos, childIndex );
        }
        else if ( childIndex > 0 )
        {
            leftFusion( pos, childIndex );
        }
        else
        {
            rightFusion( pos, childIndex );
        }
    }

    /**
     * Performs a left transfer on the underflowing node
     *
     * @param pos
     * @param childInd
     */
    public void leftTransfer( TFNode pos, int childInd )
    {
        TFNode par = pos.getParent();
        TFNode rightSib = par.getChild( childInd + 1 );
        TFNode tempKid;

        pos.addItem( 0, par.getItem( childInd ) );
        par.replaceItem( childInd, rightSib.getItem( 0 ) );
        tempKid = rightSib.getChild( 0 );
        pos.setChild( 1, tempKid );
        if ( tempKid != null )
        {
            tempKid.setParent( pos );
        }
        rightSib.removeItem( 0 );
    }

    /**
     * Performs a right transfer on the underflowing node
     *
     * @param pos
     * @param childInd
     */
    public void rightTransfer( TFNode pos, int childInd )
    {
        TFNode par = pos.getParent();
        TFNode leftSib = par.getChild( childInd - 1 );
        TFNode tempKid;

        pos.insertItem( 0, par.getItem( childInd-1 ) );
        tempKid = leftSib.getChild( leftSib.getNumItems() );
        par.replaceItem( childInd-1, leftSib.deleteItem( leftSib.getNumItems() - 1 ) );
        pos.setChild( 0, tempKid );
        if ( tempKid != null )
        {
            tempKid.setParent( pos );
        }
    }

    /**
     * Performs a right fusion on the underflowing node
     *
     * @param pos
     * @param childInd
     */
    public void rightFusion( TFNode pos, int childInd )
    {
        TFNode par = pos.getParent();
        TFNode rightSib = par.getChild( childInd + 1 );
        Item itm = par.removeItem( childInd );

        rightSib.insertItem( 0, itm );
        if ( pos.getChild( 0 ) != null )
        {
            rightSib.setChild( 0, pos.getChild( 0 ) );
            rightSib.getChild( 0 ).setParent( rightSib );
        }

        if ( par.getNumItems() == 0 )
        {
            handleUnderflow( par );
        }
    }

    /**
     * Performs a left fusion on the underflowing
     *
     * @param pos
     * @param childInd
     */
    public void leftFusion( TFNode pos, int childInd )
    {
        TFNode par = pos.getParent();
        TFNode leftSib = par.getChild( childInd - 1 );
        Item itm = par.removeItem( childInd-1 );

        par.setChild( childInd - 1, leftSib );
        leftSib.addItem( leftSib.getNumItems(), itm );
        if ( pos.getChild( 0 ) != null )
        {
            leftSib.setChild( leftSib.getNumItems(), pos.getChild( 0 ) );
            leftSib.getChild( leftSib.getNumItems() ).setParent( leftSib );
        }

        if ( par.getNumItems() == 0 )
        {
            handleUnderflow( par );
        }
    }

    public int WCAI( TFNode child )
    {
        TFNode par = child.getParent();
        int childNum = -1;

        if ( par != null )
        {
            for ( int k = 0; k <= par.getNumItems(); ++k )
            {
                if ( child == par.getChild( k ) )
                {
                    childNum = k;
                    break;
                }
            }
        }

        return childNum;
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
        int feRslt;
        TFNode pos = tfn;

        ffgeRslt = FFGE( itm, pos );
        feRslt = FE( itm, pos );

        if ( pos.getChild( ffgeRslt ) != null && feRslt == -1 )
        {
            pos = pos.getChild( ffgeRslt );
            pos = searchTree( itm, pos );
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
        if ( itm == null || itm.key() == null || itm.element() == null )
        {
            throw new TFNodeException( "Item passed is null" );
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

    public int FE( Item itm, TFNode tfn ) throws TFNodeException
    {
        if ( tfn == null )
        {
            throw new TFNodeException( "TFNode passed is null" );
        }

        TFNode pos = tfn;
        int index = -1;

        int cnt = 0;

        while ( cnt < pos.getNumItems() )  //Iterate thru TFN's Item array
        {
            if ( treeComp.isEqual( itm.key(), pos.getItem( cnt ).key() ) )  //If an Item's key in pos >= itm's key
            {
                index = cnt;
                break;    //If fG, break out otherwise we keep setting index
            }

            ++cnt;
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
        TFNode tracer;
        Item itm = new Item( key, 0 );
        Item ret;
        int index;

        if ( !treeComp.isComparable( key ) )
        {
            throw new ElementNotFoundException( "Key passed is invalid and could not be located" );
        }
        if ( isEmpty() )
        {
            throw new ElementNotFoundException( "There are no elements to remove" );
        }

        pos = searchTree( itm, treeRoot );
        index = FE( itm, pos );

        if ( index == -1 )  //Err state
        {
            throw new ElementNotFoundException( "Element not found" );
        }

        ret = pos.getItem( index );
        if ( pos.getChild( 0 ) != null )  //NOT a leaf
        {
            tracer = pos.getChild( index + 1 );
            while ( tracer.getChild( 0 ) != null )  //Not a leaf
            {
                tracer = tracer.getChild( 0 );
            }

            pos.replaceItem( index, tracer.removeItem( 0 ) );
            if ( tracer.getNumItems() == 0 )
            {
                handleUnderflow( tracer );
            }
        }
        else
        {
            pos.removeItem( index );
            if ( pos.getNumItems() == 0 )
            {
                handleUnderflow( pos );
            }
        }

        return ret.element();
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
        final int TEST_SIZE = 1000000;
        Random rand = new Random( );
        int[] nums = new int[ TEST_SIZE ];

        for ( int i = 0; i < TEST_SIZE; ++i )
        {
            int num = rand.nextInt( TEST_SIZE / 5 );
            nums[ i ] = num;
            myTree.insertElement( new Integer( num ), new Integer( num ) );
            //myTree.printAllElements();
            //System.out.println();
            //myTree.checkTree();
        }
        myTree.printAllElements();
        System.out.println( "removing" );

        for ( int i = 0; i < TEST_SIZE; ++i )
        {
            int num = nums[ i ];
            if ( i > TEST_SIZE - 25 )
            {
                System.out.println( "removing " + num );
            }
            int out = ( Integer ) myTree.removeElement( new Integer( num ) );
            if ( out != num )
            {
                throw new TwoFourTreeException( "main: wrong element removed" );  //This line is evil
            }
            if ( i > TEST_SIZE - 25 )
            {
                myTree.printAllElements();
            }
        }
        System.out.println( "done" );
    }
}
