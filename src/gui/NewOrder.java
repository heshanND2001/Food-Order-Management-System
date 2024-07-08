/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import java.awt.Color;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import model.Product;

/**
 *
 * @author User
 */

interface Order {

    @Override
    public String toString();
}

class DineInOrder implements Order {

    private String invoiceTotal;

    public DineInOrder(DineInOrderBuilder builder) {
        this.invoiceTotal = builder.getinvoiceTotal();
    }

    public DineInOrder arrangeFoods() {
        return this;
    }

    @Override
    public String toString() {
        return "DineInOrder total=" + invoiceTotal;
    }

    static class DineInOrderBuilder {

        private String invoiceTotal;
        private NewOrder newOrder;

        public DineInOrderBuilder(NewOrder addOrder) {
            this.newOrder = addOrder;
        }

        public DineInOrderBuilder arrangeFoods() {
            this.invoiceTotal = newOrder.getTotal();
            return this;
        }

        private String getinvoiceTotal() {
            return invoiceTotal;
        }

        public DineInOrder Build() {
            return new DineInOrder(this);
        }

    }

}

class TakeAwayOrder implements Order {

    private String invoiceTotal;

    public TakeAwayOrder(TakeAwayOrderBuilder builder) {
        this.invoiceTotal = builder.getinvoiceTotal();
    }

    public TakeAwayOrder arrangeFoods() {
        return this;
    }

    @Override
    public String toString() {
        return "TakeAwayOrder total=" + invoiceTotal;
    }

    static class TakeAwayOrderBuilder {

        private String invoiceTotal;
        private NewOrder newOrder;

        public TakeAwayOrderBuilder(NewOrder addOrder) {
            this.newOrder = addOrder;
        }

        public TakeAwayOrderBuilder packingFoods() {
            this.invoiceTotal = newOrder.getTotal();
            return this;
        }

        private String getinvoiceTotal() {
            return invoiceTotal;
        }

        public TakeAwayOrder Build() {
            return new TakeAwayOrder(this);
        }

    }

}

class DeliveryOrder implements Order {

    private String invoiceTotal;

    public DeliveryOrder(DeliveryOrderBuilder builder) {
        this.invoiceTotal = builder.getinvoiceTotal();
    }

    public DeliveryOrder arrangeFoods() {
        return this;
    }

    @Override
    public String toString() {
        return "DeliveryOrder total=" + invoiceTotal;
    }

    static class DeliveryOrderBuilder {

        private String invoiceTotal;
        private NewOrder newOrder;

        public DeliveryOrderBuilder(NewOrder newOrder) {
            this.newOrder = newOrder;
        }

        public DeliveryOrderBuilder packingFoods() {
            this.invoiceTotal = newOrder.getTotal();
            return this;
        }

        private String getinvoiceTotal() {
            return invoiceTotal;
        }

        public DeliveryOrder Build() {
            return new DeliveryOrder(this);
        }

    }

}

class Request {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    void setProducts(List<Product> productsArray) {
        this.products = productsArray;
    }

}

abstract class Filter {

    protected Filter filter;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public abstract void filter(Request request);
}

class OrderAccept extends Filter {

    @Override
    public void filter(Request request) {
        if (request.getProducts().size() > 0) {
            this.filter.filter(request);
        } else {
            System.out.println("Don't have Product");
        }
    }
}

class CookingFood extends Filter {

    @Override
    public void filter(Request request) {

        try {
            boolean isTrue = true;
            for (Product item : request.getProducts()) {

                ResultSet rs = MySQL.search("SELECT* FROM product WHERE `id` = '" + item.getId() + "'");

                while (rs.next()) {
                    if (Integer.parseInt(rs.getString("qty")) >= item.getQty()) {

                    } else {
                        isTrue = false;
                        break;
                    }
                }

            }

            if (isTrue) {
                this.filter.filter(request);
            } else {
                System.out.println("Not enought qty");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class PackingOrder extends Filter {

    @Override
    public void filter(Request request) {
        if (true) {
            this.filter.filter(request);
        } else {
            System.out.println("Not packed");
        }
    }
}

class HandoverDriver extends Filter {

    @Override
    public void filter(Request request) {
        if (true) {
            System.out.println("Success");
        } else {
            System.out.println("Not hand overef");
        }
    }
}

public class NewOrder extends javax.swing.JFrame {
    
    private static final HashMap<String, Toppings> pool = new HashMap<>();

    DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Creates new form NewOrder
     */
    public NewOrder() {
        initComponents();
    }
    
    abstract class Toppings {

        private String name;
        private double price = 0;

        public Toppings(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

    }

    private Toppings getToppingObj(String ingredient) {
        Toppings toppingObj = pool.get(ingredient);
        try {
            if (toppingObj == null) {

                ResultSet rs = MySQL.search("SELECT * FROM ingredient WHERE name = '" + ingredient + "'");

                if (rs.next()) {
                    toppingObj = new Toppings(ingredient, Double.parseDouble(rs.getString("price"))) {
                    };
                    pool.put(ingredient, toppingObj);
                } else {
                    System.out.println(ingredient);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toppingObj;
    }

    public String getTotal() {

        double total = 0;

        for (int i = 0; i < jTable3.getRowCount(); i++) {

            String[] toppings = jTable3.getValueAt(i, 3).toString().split(",");

            if (toppings.length > 0) {
                for (String ingredient : toppings) {

                    if (!ingredient.isEmpty()) {
                        Toppings toppingObj = getToppingObj(ingredient.trim());
                        total += toppingObj.getPrice() * Float.parseFloat(jTable3.getValueAt(i, 5).toString());
                    }

                }
            }

            total += Double.parseDouble(jTable3.getValueAt(i, 2).toString()) * Float.parseFloat(jTable3.getValueAt(i, 5).toString());
        }

        return String.valueOf(total);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("~New Order~");

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel1.setText("Products");

        jTable1.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Price", "qty"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel2.setText("Ingredients");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Price"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 16)); // NOI18N
        jLabel3.setText("Quantity :");

        jTextField3.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N

        jButton1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jButton1.setText("Add to Invoice");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Item Price", "Toppings", "Topping Price", "Qty", "Sub Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 16)); // NOI18N
        jLabel4.setText("Total : Rs. ");

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 16)); // NOI18N
        jLabel5.setText("00.00");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 16)); // NOI18N
        jLabel7.setText("Order Type : ");

        jComboBox2.setFont(new java.awt.Font("Yu Gothic UI", 0, 16)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dine-in order", "Take away", "Online order", " " }));
        jComboBox2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel8.setText("Payment : ");

        jTextField4.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4KeyTyped(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel9.setText("Balance :");

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel10.setText("00.00");

        jButton2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jButton2.setText("Submit");
        jButton2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton3.setText("Add Item");
        jButton3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton4.setText("Add Ingredients");
        jButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel6.setText("Commend");

        jButton5.setFont(new java.awt.Font("Yu Gothic UI", 1, 16)); // NOI18N
        jButton5.setText("Run Commend");
        jButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(23, 23, 23)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(34, 34, 34)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(60, 60, 60))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new SelectProduct(this).setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       new SelectIngredients(this).setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jTable1.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select product item.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (!Pattern.compile("[1-9][0-9]*").matcher(jTextField3.getText()).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (Integer.parseInt(jTextField3.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            float qty = Float.valueOf(jTextField3.getText());

            DefaultTableModel dtm = (DefaultTableModel) jTable3.getModel();

            Vector v = new Vector();

            for (int i = 0; i < jTable1.getRowCount(); i++) {
                v.add(jTable1.getValueAt(i, 0).toString());
                v.add(jTable1.getValueAt(i, 1).toString());
                v.add(jTable1.getValueAt(i, 2).toString());
            }

            float toppingPrice = 0;
            String toppings = "";

            for (int i = 0; i < jTable2.getRowCount(); i++) {
                if (i != 0) {
                    toppings += ", ";
                }
                toppings += jTable2.getValueAt(i, 1).toString();
                toppingPrice += Float.parseFloat(jTable2.getValueAt(i, 2).toString());
            }

            v.add(toppings);
            v.add(toppingPrice);
            v.add(qty);
            v.add((toppingPrice + Float.parseFloat(jTable1.getValueAt(0, 2).toString())) * qty);

            dtm.addRow(v);

            DefaultTableModel dtm1 = (DefaultTableModel) jTable1.getModel();
            dtm1.setRowCount(0);

            DefaultTableModel dtm2 = (DefaultTableModel) jTable2.getModel();
            dtm2.setRowCount(0);

            jTextField3.setText("");
            jLabel5.setText(getTotal());

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        
         if (jTextField4.getText().isEmpty()) {
            jLabel10.setText("0.00");
            jLabel10.setForeground(Color.BLUE);
        } else {
            String total = jLabel5.getText();
            String payment = jTextField4.getText();

            double balance = Double.parseDouble(payment) - Double.parseDouble(total);
            if (balance < 0) {
                jLabel10.setForeground(Color.RED);
            } else {
                jLabel10.setForeground(Color.BLUE);
            }
            jLabel10.setText(df.format(balance));
        }
        
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyTyped
        String price = jTextField4.getText();
        String text = price + evt.getKeyChar();

        if (!Pattern.compile("0|0[.]|0[.][1-9]*|[1-9]|[1-9][0-9]*|[1-9][0-9]*[.]|[1-9][0-9]*[.][0-9]*").matcher(text).matches()) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField4KeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        String[] expressions = jTextArea1.getText().split(",");

        if (expressions.length <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter command.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                for (String expression : expressions) {
                    if (!expression.isEmpty()) {

                        ResultSet rs1 = MySQL.search("SELECT * FROM `product`");

                        String query = "SELECT * FROM `ingredient` WHERE `id` != '0' ";

                        for (int i = 0; i < jTable2.getRowCount(); i++) {
                            query += " AND `id` != '" + jTable2.getValueAt(i, 0).toString() + "' ";
                        }

                        ResultSet rs2 = MySQL.search(query);

                        while (rs1.next()) {

                            if (expression.trim().toLowerCase().contains(rs1.getString("name").toLowerCase())) {
                                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                                dtm.setRowCount(0);
                                Vector v = new Vector();
                                v.add(rs1.getString("id"));
                                v.add(rs1.getString("name"));
                                v.add(rs1.getString("price"));
                                dtm.addRow(v);

                                break;
                            } else {
                                while (rs2.next()) {
                                    if (expression.trim().toLowerCase().contains(rs2.getString("name").toLowerCase())) {
                                        DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();

                                        Vector v = new Vector();
                                        v.add(rs2.getString("id"));
                                        v.add(rs2.getString("name"));
                                        v.add(rs2.getString("price"));
                                        dtm.addRow(v);

                                        break;
                                    } else {
                                        if (expression.trim().contains("qty") || expression.trim().contains("quantity")) {
                                            Matcher matcher = Pattern.compile("\\b\\d+\\b").matcher(expression.trim());
                                            if (matcher.find()) {
                                                String qty = matcher.group();
                                                jTextField3.setText(qty);
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        if (jTable3.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one product.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (jTextField4.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter payment.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            double balance = Double.parseDouble(getTotal()) - Double.parseDouble(jTextField4.getText());

            if (balance <= 0) {

                Order order = null;
                if (jComboBox2.getSelectedItem() == "Dine-in order") {
                    order = new DineInOrder.DineInOrderBuilder(this).arrangeFoods().Build();
                } else if (jComboBox2.getSelectedItem() == "Take away") {
                    order = new TakeAwayOrder.TakeAwayOrderBuilder(this).packingFoods().Build();
                } else if (jComboBox2.getSelectedItem() == "Online order") {
                    order = new DeliveryOrder.DeliveryOrderBuilder(this).packingFoods().Build();
                }

                JOptionPane.showMessageDialog(this, order.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
            }

            List<Product> productsArray = new ArrayList<Product>();

            for (int i = 0; i < jTable3.getRowCount(); i++) {

                String[] toppings = jTable3.getValueAt(i, 3).toString().split(",");

                Product p = new Product(jTable3.getValueAt(i, 0).toString(), jTable3.getValueAt(i, 1).toString(), Double.parseDouble(jTable3.getValueAt(i, 6).toString()), Float.parseFloat(jTable3.getValueAt(i, 5).toString()));

                productsArray.add(p);
            }

            Request request = new Request();
            request.setProducts(productsArray);

            OrderAccept orderAccept = new OrderAccept();
            CookingFood cookingFood = new CookingFood();
            PackingOrder packingOrder = new PackingOrder();
            HandoverDriver driver = new HandoverDriver();

            orderAccept.setFilter(cookingFood);
            cookingFood.setFilter(packingOrder);
            packingOrder.setFilter(driver);
            orderAccept.filter(request);

        }

        
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewOrder().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTable jTable1;
    public javax.swing.JTable jTable2;
    public javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
