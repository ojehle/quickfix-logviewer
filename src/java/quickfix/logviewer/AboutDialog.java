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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDialog extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GridBagConstraints constraints = new GridBagConstraints();
	
	AboutDialog(JFrame owner) {
		super(owner, "About");
		setResizable( false );
		setSize( 320, 100 );
		
		setLayout( new GridBagLayout() );
		constraints.fill = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.gridy = 0;
		
		String version = Properties.getInstance().version();
		
		addLabel( "QuickFIX Log Viewer v" + version );
		addLabel( "Copyright 2004-2006 quickfixengine.org" );
		addLabel( "www.quickfixengine.org");
		addLabel( "ask@quickfixengine.org");
	}
	
	private void addLabel( String text ) {
		JLabel label = new JLabel( text );
		label.setBackground( Color.WHITE );
		label.setForeground( Color.BLACK );
		constraints.gridy++;
		getContentPane().add( label, constraints );
	}
}
