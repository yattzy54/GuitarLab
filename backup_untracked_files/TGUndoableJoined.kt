package app.tuxguitar.android.action.listener.undoable
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.editor.undo.*
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase
import app.tuxguitar.util.TGContext
class TGUndoableJoined(context:TGContext):TGUndoableEditBase(context){private val edits=mutableListOf<TGUndoableEdit>();private var state=TGUndoableCaretState(context);private var action=UNDO_ACTION;fun addUndoableEdit(e:TGUndoableEdit){edits.add(e)};override fun redo(c:TGActionContext){edits.forEach{it.redo(c)};state.redo(c);action=UNDO_ACTION};override fun undo(c:TGActionContext){edits.asReversed().forEach{it.undo(c)};state.undo(c);action=REDO_ACTION};override fun canRedo()=action==REDO_ACTION;override fun canUndo()=action==UNDO_ACTION;fun endUndo()=also{state.endUndo()};fun isEmpty()=edits.isEmpty()}
