/****************************************************************************
** Copyright (c) quickfixengine.org  All rights reserved.
**
** This file is part of the QuickFIX FIX Engine
**
** This file may be distributed under the terms of the quickfixengine.org
** license as defined by quickfixengine.org and appearing in the file
** LICENSE included in the packaging of this file.
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
** See http://www.quickfixengine.org/LICENSE for licensing information.
**
** Contact ask@quickfixengine.org if any conditions of this licensing are
** not clear to you.
**
****************************************************************************/

package quickfix.logviewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import quickfix.DataDictionary;
import quickfix.StringField;

public class CustomFilterDialog extends Dialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ButtonPanel buttonPanel = new ButtonPanel();
	private ArrayList<FilterPanel> filterPanels = new ArrayList<FilterPanel>();
	private ArrayList<FieldFilter> filter = null;

	class ComboBoxItem extends Object {
		private Integer tag = null;
		private DataDictionary dataDictionary = null;

		ComboBoxItem(Integer aTag, DataDictionary aDataDictionary) {
			tag = aTag;
			dataDictionary = aDataDictionary;
		}

		public String toString() {
			return dataDictionary.getFieldName(tag.intValue()) + "(" + tag + ")";
		}
	}

	class OperatorComboBox extends JComboBox<String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		OperatorComboBox() {
			this.addItem("=");
			this.addItem("!=");
			this.addItem("<");
			this.addItem("<=");
			this.addItem(">");
			this.addItem(">=");
			this.addItem("contains");
		}

		public void setOperator(int operatorId) {
			String x = convertOperator(operatorId);
			for (int i = 0; i < this.getItemCount(); i++) {
				if (x.equalsIgnoreCase(this.getItemAt(i))) {
					this.setSelectedIndex(i);
				}
			}

		}
	}

	class FilterPanel extends JPanel implements ChangeListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox checkBox = new JCheckBox();
		private JComboBox<ComboBoxItem> comboBox = new JComboBox<ComboBoxItem>();
		private OperatorComboBox operatorComboBox = new OperatorComboBox();
		private JTextField textField = new JTextField();

		FilterPanel(SortedSet<Integer> tags, DataDictionary dataDictionary) {

			setLayout(new FlowLayout());
			enablePanel(false);

			checkBox.setPreferredSize(new Dimension(25, 25));
			add(checkBox);
			comboBox.setPreferredSize(new Dimension(200, 25));
			add(comboBox);
			operatorComboBox.setPreferredSize(new Dimension(90, 25));
			add(operatorComboBox);
			textField.setPreferredSize(new Dimension(250, 25));
			add(textField);
			add(new JLabel());

			TreeSet<Integer> sortedTags = new TreeSet<Integer>(tags);
			Iterator<Integer> i = sortedTags.iterator();
			while (i.hasNext()) {
				comboBox.addItem(new ComboBoxItem((Integer) i.next(), dataDictionary));
			}

			checkBox.addChangeListener(this);
		}

		private void enablePanel(boolean b) {
			comboBox.setEnabled(b);
			textField.setEnabled(b);
		}

		public void stateChanged(ChangeEvent e) {
			enablePanel(checkBox.isSelected());
		}

		public void set(FieldFilter fieldFilter) {
			int t = fieldFilter.getTag();
			for (int i = 0; i < comboBox.getItemCount(); ++i) {
				ComboBoxItem item = (ComboBoxItem) comboBox.getItemAt(i);
				if (item.tag.intValue() == t) {
					comboBox.setSelectedIndex(i);
					operatorComboBox.setSelectedIndex(fieldFilter.getOperator());
					textField.setText(fieldFilter.getValue());
					checkBox.setSelected(true);
				}
			}
		}
	}

	class ButtonPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JButton cancel = new JButton("Cancel");
		private JButton apply = new JButton("Apply");
		private JButton load = new JButton("Load");
		private JButton save = new JButton("Save");

		ButtonPanel() {
			setLayout(new GridBagLayout());

			GridBagConstraints constraints = new GridBagConstraints();
			add(load, constraints);
			add(save, constraints);
			add(cancel, constraints);
			add(apply, constraints);
		}
	}

	public CustomFilterDialog(JFrame owner, ArrayList<FieldFilter> filter, SortedSet<Integer> tags,
			DataDictionary dataDictionary) throws HeadlessException {
		super(owner, "Custom Filter");

		int rows = 10;
		Dimension dimension = new Dimension();
		dimension.height = rows * 35 + 60;
		dimension.width = 600;
		setSize(dimension);
		setResizable(false);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		for (int i = 1; i <= rows; ++i) {
			constraints.weightx = 10;
			constraints.gridx = 1;
			constraints.weighty = 1;
			constraints.gridy = i;
			FilterPanel filterPanel = new FilterPanel(tags, dataDictionary);
			if (filter.size() >= i) {
				FieldFilter fieldFilter = (FieldFilter) filter.get(i - 1);
				filterPanel.set(fieldFilter);
			}
			filterPanels.add(filterPanel);
			getContentPane().add(filterPanel, constraints);
		}

		constraints.weighty = 1;
		constraints.gridy = rows + 1;
		constraints.weightx = 10;
		constraints.gridx = 1;
		getContentPane().add(buttonPanel, constraints);

		buttonPanel.cancel.addActionListener(this);
		buttonPanel.apply.addActionListener(this);
		buttonPanel.save.addActionListener(this);
		buttonPanel.load.addActionListener(this);
	}


	private void loadFilter() {
		/* reset all */

		Iterator<FilterPanel> i = filterPanels.iterator();
		while (i.hasNext()) {
			FilterPanel filterPanel = (FilterPanel) i.next();
			
			filterPanel.comboBox.setSelectedIndex(-1);
			filterPanel.operatorComboBox.setSelectedIndex(-1);
			filterPanel.textField.setText("");
		}
		
		if (filter == null)
			return;

		i = filterPanels.iterator();

		for (FieldFilter x : filter) {

		 if (i.hasNext()) {
			FilterPanel filterPanel = (FilterPanel) i.next();
				filterPanel.set(x);
		 }
		 }
	}

	private void saveFilter() {
		filter = new ArrayList<FieldFilter>();
		Iterator<FilterPanel> i = filterPanels.iterator();
		while (i.hasNext()) {
			FilterPanel filterPanel = (FilterPanel) i.next();
			if (filterPanel.checkBox.isSelected()) {
				ComboBoxItem item = (ComboBoxItem) filterPanel.comboBox.getSelectedItem();
				int tag = item.tag.intValue();
				String value = filterPanel.textField.getText();
				FieldFilter fieldFilter = new FieldFilter(new StringField(tag, value),
						convertOperator((String) filterPanel.operatorComboBox.getSelectedItem()));
				filter.add(fieldFilter);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonPanel.load) {
			loadFilterDialog();
			loadFilter();
			return;
		}
		if (e.getSource() == buttonPanel.save) {
			saveFilter();
			saveFilterDialog();
			return;
		}
		if (e.getSource() == buttonPanel.cancel) {
			setVisible(false);
			filter = null;
		} else if (e.getSource() == buttonPanel.apply) {
			saveFilter();
			setVisible(false);
		}
	}

	public static String convertOperator(int stringOperator) {
		switch (stringOperator) {
		case FieldFilter.EQUAL:
			return "=";
		case FieldFilter.NOT_EQUAL:
			return "!=";
		case FieldFilter.LESS_THAN:
			return "<";
		case FieldFilter.LESS_THAN_OR_EQUAL:
			return "<=";
		case FieldFilter.GREATER_THAN:
			return ">";
		case FieldFilter.GREATER_THAN_OR_EQUAL:
			return ">=";
		case FieldFilter.CONTAINS:
			return "contains";
		}
		return null;
	}

	public static int convertOperator(String stringOperator) {
		if (stringOperator.equals("="))
			return FieldFilter.EQUAL;
		if (stringOperator.equals("!="))
			return FieldFilter.NOT_EQUAL;
		if (stringOperator.equals("<"))
			return FieldFilter.LESS_THAN;
		if (stringOperator.equals("<="))
			return FieldFilter.LESS_THAN_OR_EQUAL;
		if (stringOperator.equals(">"))
			return FieldFilter.GREATER_THAN;
		if (stringOperator.equals(">="))
			return FieldFilter.GREATER_THAN_OR_EQUAL;
		if (stringOperator.equals("contains"))
			return FieldFilter.CONTAINS;
		return FieldFilter.CONTAINS;
	}

	public ArrayList<FieldFilter> getFilter() {
		return filter;
	}

	public void loadFilterDialog() {
		File f;
		f = FileSystemView.getFileSystemView().getHomeDirectory();
		f = new File(f, ".quickfix-logviewer");
		if (!f.exists())
			f.mkdir();

		JFileChooser jfc = new JFileChooser(f);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			f = jfc.getSelectedFile();
		} else
			return;

		try {
			String l = null;
			if (filter != null) {
				filter.clear();
			}else {
				filter = new ArrayList<FieldFilter>();
			}
			BufferedReader br = new BufferedReader(new FileReader(f));
			l = br.readLine();
			while (l != null && l.length() > 0) {
				String values[] = l.split("=");
				FieldFilter fieldFilter = new FieldFilter(
						new StringField(Integer.parseUnsignedInt(values[0]), values[1]),
						Integer.parseUnsignedInt(values[2]));
				filter.add(fieldFilter);
				l = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveFilterDialog() {
		File f;
		f = FileSystemView.getFileSystemView().getHomeDirectory();
		f = new File(f, ".quickfix-logviewer");
		if (!f.exists())
			f.mkdir();

		JFileChooser jfc = new JFileChooser(f);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			f = jfc.getSelectedFile();
		} else
			return;

		StringBuffer b = new StringBuffer();
		for (FieldFilter ff : getFilter()) {
			b.append(ff.toString());
			b.append("\n");
		}
		f.delete();
		try {
			FileWriter fw = new FileWriter(f);
			fw.append(b.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}
}
