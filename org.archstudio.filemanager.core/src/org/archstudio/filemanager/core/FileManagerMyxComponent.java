package org.archstudio.filemanager.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.archstudio.filemanager.CantOpenFileException;
import org.archstudio.xarchadt.IXArchADTModelListener;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.XArchADTModelEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

/**
 * Myx brick: "File Manager Impl"
 * 
 * @see org.archstudio.filemanager.core.FileManagerMyxComponentStub
 * @generated
 */
public class FileManagerMyxComponent extends
		org.archstudio.filemanager.core.FileManagerMyxComponentStub {

	protected Set<ObjRef> dirtySet = Collections
			.synchronizedSet(new HashSet<ObjRef>());

	//Keeps track of which tools have which documents open. When no tools have
	//a document open, it is closed in xArchADT.
	protected Map<ObjRef, List<String>> openerMap = Collections
			.synchronizedMap(new HashMap<ObjRef, List<String>>());

	public FileManagerMyxComponent() {
	}

	@Override
	public void handleXArchADTModelEvent(XArchADTModelEvent evt) {
		for (Object o : myxRegistry.getObjects(this)) {
			if (o instanceof IXArchADTModelListener) {
				((IXArchADTModelListener) o).handleXArchADTModelEvent(evt);
			}
		}
		ObjRef documentRootRef = xarch.getDocumentRootRef(evt.getSource());
		makeDirty(documentRootRef);
	}

	private static URI getURI(IFile f) {
		return URI.createFileURI(f.getFullPath().toPortableString());
	}

	private static URI getURI(java.io.File f) {
		return URI.createFileURI(f.getPath());
	}

	@Override
	public boolean isOpen(IFile f) {
		URI uri = getURI(f);
		return xarch.getOpenURIs().contains(uri);
	}

	@Override
	public ObjRef getDocumentRootRef(IFile f) {
		URI uri = getURI(f);
		return xarch.getDocumentRootRef(uri);
	}

	@Override
	public ObjRef open(String toolID, IFile f) throws CantOpenFileException {
		InputStream is = null;
		OutputStream os = null;
		URI uri = null;
		try {
			uri = getURI(f);
			ObjRef documentRootRef = xarch.load(uri);

			List<String> toolList = openerMap.get(documentRootRef);
			if (toolList == null) {
				toolList = new ArrayList<String>();
			}
			toolList.add(toolID);
			openerMap.put(documentRootRef, toolList);

			return documentRootRef;
		} catch (Exception e) {
			throw new CantOpenFileException("Can't open file: " + uri, e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e2) {
			}
		}
	}

	@Override
	public ObjRef open(String toolID, java.io.File f)
			throws CantOpenFileException {
		InputStream is = null;
		OutputStream os = null;
		URI uri = null;
		try {
			uri = getURI(f);
			ObjRef documentRootRef = xarch.load(uri);

			List<String> toolList = openerMap.get(documentRootRef);
			if (toolList == null) {
				toolList = new ArrayList<String>();
			}
			toolList.add(toolID);
			openerMap.put(documentRootRef, toolList);

			return documentRootRef;
		} catch (Exception e) {
			throw new CantOpenFileException("Can't open file: " + uri, e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e2) {
			}
		}
	}

	@Override
	public void close(String toolID, ObjRef documentRootRef) {
		List<String> toolList = openerMap.get(documentRootRef);
		URI uri = xarch.getURI(documentRootRef);
		if (toolList == null) {
			xarch.close(uri);
			return;
		}

		toolList.remove(toolID);
		if (toolList.size() == 0) {
			xarch.close(uri);
			openerMap.remove(documentRootRef);
		}
	}

	@Override
	public void makeDirty(ObjRef xArchRef) {
		if (dirtySet.contains(xArchRef)) {
			return;
		}
		dirtySet.add(xArchRef);
		if (fileManagerEvents != null) {
			fileManagerEvents.fileDirtyStateChanged(xArchRef, true);
		}
	}

	@Override
	public void makeClean(ObjRef xArchRef) {
		if (!dirtySet.contains(xArchRef)) {
			return;
		}
		dirtySet.remove(xArchRef);
		if (fileManagerEvents != null) {
			fileManagerEvents.fileDirtyStateChanged(xArchRef, false);
		}
	}

	@Override
	public boolean isDirty(ObjRef xArchRef) {
		return dirtySet.contains(xArchRef);
	}

	@Override
	public void save(ObjRef xArchRef, IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask("Saving File", 100);
			monitor.worked(1);
		}
		if (fileManagerEvents != null) {
			monitor.subTask("Notifying Editors");
			monitor.worked(2);
			try {
				fileManagerEvents.fileSaving(xArchRef, monitor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (monitor != null) {
			monitor.worked(80);
		}

		URI uri = xarch.getURI(xArchRef);
		try {
			xarch.save(uri);
			makeClean(xArchRef);
		} catch (IOException ioe) {
			//TODO: Handle
			ioe.printStackTrace();
		}
		if (monitor != null) {
			monitor.worked(100);
		}
		monitor.done();
	}

	@Override
	public void saveAs(ObjRef xArchRef, IFile f) {
	}
}