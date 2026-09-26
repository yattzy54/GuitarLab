package app.tuxguitar.android.action.listener.undoable
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.editor.undo.*
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase
import app.tuxguitar.util.TGContext
class TGUndoableCaretState(context:TGContext):TGUndoableEditBase(context){override fun redo(c:TGActionContext){if(!canRedo())throw TGCannotRedoException();action=UNDO_ACTION};override fun undo(c:TGActionContext){if(!canUndo())throw TGCannotUndoException();action=REDO_ACTION};override fun canRedo()=action==REDO_ACTION;override fun canUndo()=action==UNDO_ACTION;fun startUndo(){action=UNDO_ACTION};fun endUndo(){};private var action=UNDO_ACTION}
