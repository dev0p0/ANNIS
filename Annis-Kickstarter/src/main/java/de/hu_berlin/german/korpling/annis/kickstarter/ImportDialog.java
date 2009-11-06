/*
 *  Copyright 2009 thomas.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

/*
 * ImportDialog.java
 *
 * Created on 26.10.2009, 22:01:47
 */
package de.hu_berlin.german.korpling.annis.kickstarter;

import annis.administration.CorpusAdministration;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author thomas
 */
public class ImportDialog extends javax.swing.JDialog
{

  private CorpusAdministration corpusAdministration;
  private SwingWorker<String, Void> worker;

  /** Creates new form ImportDialog */
  public ImportDialog(java.awt.Frame parent, boolean modal, CorpusAdministration corpusAdmin)
  {
    super(parent, modal);

    this.corpusAdministration = corpusAdmin;

    initComponents();

    worker = new SwingWorker<String, Void>()
    {

      @Override
      protected String doInBackground() throws Exception
      {
        try
        {
          corpusAdministration.importCorpora(txtInputDir.getText());
        }
        catch(Exception ex)
        {
          return ex.getMessage();
        }
        return "";
      }

      @Override
      protected void done()
      {
        btOk.setEnabled(true);
        btSearchInputDir.setEnabled(true);
        txtInputDir.setEnabled(true);
        pbImport.setIndeterminate(false);
        try
        {
          if("".equals(this.get()))
          {
            JOptionPane.showMessageDialog(null,
              "Corpus imported.", "INFO", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
          }
          else
          {
            JOptionPane.showMessageDialog(null,
              "Import failed: " + this.get(), "ERROR", JOptionPane.ERROR_MESSAGE);
          }
        }
        catch(Exception ex)
        {
          Logger.getLogger(ImportDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    fileChooser = new javax.swing.JFileChooser();
    jLabel1 = new javax.swing.JLabel();
    txtInputDir = new javax.swing.JTextField();
    btCancel = new javax.swing.JButton();
    btOk = new javax.swing.JButton();
    btSearchInputDir = new javax.swing.JButton();
    pbImport = new javax.swing.JProgressBar();

    fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Import - Annis² Kickstarter");
    setLocationByPlatform(true);

    jLabel1.setText("Directory to import:");

    btCancel.setMnemonic('c');
    btCancel.setText("Cancel");
    btCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btCancelActionPerformed(evt);
      }
    });

    btOk.setMnemonic('o');
    btOk.setText("Ok");
    btOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btOkActionPerformed(evt);
      }
    });

    btSearchInputDir.setText("...");
    btSearchInputDir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btSearchInputDirActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pbImport, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtInputDir, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btSearchInputDir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
            .addComponent(btOk, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(txtInputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(btSearchInputDir))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pbImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btCancel)
          .addComponent(btOk))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btCancelActionPerformed
    {//GEN-HEADEREND:event_btCancelActionPerformed

      if(!worker.isDone())
      {
        worker.cancel(true);
      }
      setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    private void btOkActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btOkActionPerformed
    {//GEN-HEADEREND:event_btOkActionPerformed

      btOk.setEnabled(false);
      btSearchInputDir.setEnabled(false);
      txtInputDir.setEnabled(false);

      pbImport.setIndeterminate(true);

      worker.execute();

    }//GEN-LAST:event_btOkActionPerformed

    private void btSearchInputDirActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btSearchInputDirActionPerformed
    {//GEN-HEADEREND:event_btSearchInputDirActionPerformed

      if(fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION)
      {
        File f = fileChooser.getSelectedFile();
        txtInputDir.setText(f.getAbsolutePath());
      }

    }//GEN-LAST:event_btSearchInputDirActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btCancel;
  private javax.swing.JButton btOk;
  private javax.swing.JButton btSearchInputDir;
  private javax.swing.JFileChooser fileChooser;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JProgressBar pbImport;
  private javax.swing.JTextField txtInputDir;
  // End of variables declaration//GEN-END:variables
}
