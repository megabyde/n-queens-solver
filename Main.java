import java.awt.event.*;
import java.awt.*;
import java.applet.*;
import java.util.*;

public class Main extends Applet {
    private static final int TILE_SIZE = 44; // Tile size in pixels

    private DrawControls controls;

    private int size; // Board size in fields
    private ArrayList<int[]> placements; // Found solutions
    private int current; // Current solution number

    private Image l;  // Empty light field
    private Image d;  // Empty dark field
    private Image ql; // Queen on a light field
    private Image qd; // Queen on a dark field

    public void init() {
        size = 8;
        placements = new ArrayList<int[]>();

        setLayout(new BorderLayout());
        controls = new DrawControls(this);
        add("South", controls);

        l = getImage(getCodeBase(), "images/l.png");
        d = getImage(getCodeBase(), "images/d.png");
        ql = getImage(getCodeBase(), "images/qll.png");
        qd = getImage(getCodeBase(), "images/qld.png");

        find();
    }

    public void paint(Graphics g) {
        // Draw the border
        g.setColor(Color.BLACK);
        g.drawRect(5, 5, size * TILE_SIZE + 1, size * TILE_SIZE + 1);

        // Draw the tiles
        for (int rank = 0; rank < size; rank++) {
            for (int file = 0; file < size; file++) {
                boolean is_light_field = (rank + file) % 2 == 0;
                boolean is_empty = placements.get(current)[rank] != file;
                Image img = is_light_field ? (is_empty ? l : ql)
                                           : (is_empty ? d : qd);
                g.drawImage(img, rank * TILE_SIZE + 5 + 1, file * TILE_SIZE + 5 + 1, this);
            }
        }

        // Show the solution number
        controls.cf.setLabel(String.format("%d / %d", current + 1, placements.size()));
    }

    private boolean check(int[] placement, int rank, int file) {
        for (int i = 0; i < rank; i++) {
            // Same file or diagonal
            if (file == placement[i] || rank == Math.abs(file - placement[i]) + i)
                return false;
        }
        return true;
    }

    private void find_rec(int[] placement, int rank) {
        for (int file = 0; file < size; file++) {
            if (check(placement, rank, file)) {
                placement[rank] = file;
                if (rank == size - 1) {
                    placements.add(placement.clone());
                }
                else {
                    find_rec(placement, rank + 1);
                }
            }
        }
    }

    public void find() {
        current = 0;
        int[] placement = new int[size];
        find_rec(placement, 0);
    }

    public void setSize(int size) {
        this.size = size;
        placements.clear();
    }

    public void showNext() {
        current = (current + 1) % placements.size();
        repaint();
    }

    public void showPrev() {
        current = (current - 1 + placements.size()) % placements.size();
        repaint();
    }
}

class DrawControls extends Panel implements ItemListener, ActionListener {

    Main target;
    Button cf;

    public DrawControls(Main target) {
        this.target = target;
        setLayout(new FlowLayout());
        setBackground(Color.lightGray);

        Button b;
        add(b = new Button("< Prev"));
        b.addActionListener(this);

        add(cf = new Button("000000000 / 000000000"));
        cf.addActionListener(this);

        add(b = new Button("Next >"));
        b.addActionListener(this);

        Choice size = new Choice();
        size.add("6");
        size.add("7");
        size.add("8");
        size.add("9");
        size.add("10");
        size.add("11");
        size.select("8");
        size.addItemListener(this);
        add(size);
    }

    public void actionPerformed(ActionEvent e) {
        String arg = e.getActionCommand();

        if (arg == "Next >") {
            target.showNext();
        } else if (arg == "< Prev") {
            target.showPrev();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof Choice) {
            String choice = (String) e.getItem();
            target.setSize(Integer.valueOf(choice));
            target.find();
            target.repaint();
        }
    }
}
 
