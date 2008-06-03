/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kitravelingsalesman;

/**
 *
 * @author bluma
 */
public class Stadt {
    int x;
    int y;
    String name;
    
    Stadt(int x, int y, String name) {
       this.x = x;
       this.y = y;
       this.name = name;
    }
    Stadt() { }

    public void setName(String name) {
        this.name = name;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    

}
