import React, { useState } from 'react';
import { SongTabScore, TabTrackInfo } from '../../types/tabPlayer';
import {
  Upload,
  Download,
  Copy,
  Check,
  FileCode,
  FileText,
  FileSpreadsheet,
  X,
  FileCheck,
} from 'lucide-react';
import {
  convertSongScoreToAlphaTex,
  exportSongToAscii,
  parseAsciiToTrack,
} from './tuxAlphaTex';

interface TuxGuitarImportExportModalProps {
  score: SongTabScore;
  isOpen: boolean;
  onClose: () => void;
  onImportScore: (score: SongTabScore) => void;
}

export const TuxGuitarImportExportModal: React.FC<TuxGuitarImportExportModalProps> = ({
  score,
  isOpen,
  onClose,
  onImportScore,
}) => {
  const [activeTab, setActiveTab] = useState<'import' | 'export'>('import');
  const [pasteContent, setPasteContent] = useState('');
  const [importType, setImportType] = useState<'ascii' | 'json'>('ascii');
  const [copied, setCopied] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  if (!isOpen) return null;

  // Handle file upload
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const fileName = file.name.toLowerCase();

    if (fileName.endsWith('.json')) {
      const reader = new FileReader();
      reader.onload = (event) => {
        try {
          const parsed = JSON.parse(event.target?.result as string);
          if (parsed && parsed.tracks && Array.isArray(parsed.tracks)) {
            onImportScore(parsed);
            setStatusMessage(`Successfully loaded project: ${parsed.title || file.name}`);
            setTimeout(() => {
              onClose();
            }, 1200);
          } else {
            setStatusMessage('Invalid project JSON structure.');
          }
        } catch (err) {
          setStatusMessage('Failed to parse JSON file.');
        }
      };
      reader.readAsText(file);
    } else {
      // Text / ASCII tab file
      const reader = new FileReader();
      reader.onload = (event) => {
        const text = event.target?.result as string;
        if (text) {
          const measures = parseAsciiToTrack(text, 'Imported Track');
          const newScore: SongTabScore = {
            id: `imp_${Date.now()}`,
            title: file.name.replace(/\.[^/.]+$/, ''),
            artist: 'Imported',
            revisionDate: new Date().toLocaleDateString(),
            defaultTempo: 120,
            tracks: [
              {
                id: `trk_imp_${Date.now()}`,
                name: 'Guitar',
                instrument: 'Distortion Guitar',
                tuningName: 'Standard E',
                tuningNotes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
                volume: 0.9,
                isMuted: false,
                isSolo: false,
                measures,
              },
            ],
          };
          onImportScore(newScore);
          setStatusMessage(`Imported ${measures.length} measures from ${file.name}!`);
          setTimeout(() => {
            onClose();
          }, 1200);
        }
      };
      reader.readAsText(file);
    }
  };

  // Handle manual paste submit
  const handleParsePaste = () => {
    if (!pasteContent.trim()) return;

    if (importType === 'json') {
      try {
        const parsed = JSON.parse(pasteContent);
        if (parsed && parsed.tracks) {
          onImportScore(parsed);
          onClose();
        } else {
          setStatusMessage('Invalid JSON format.');
        }
      } catch {
        setStatusMessage('JSON syntax error.');
      }
    } else {
      const measures = parseAsciiToTrack(pasteContent, 'Pasted Tab');
      const newScore: SongTabScore = {
        id: `imp_paste_${Date.now()}`,
        title: 'Pasted Riff',
        artist: 'User',
        revisionDate: new Date().toLocaleDateString(),
        defaultTempo: 120,
        tracks: [
          {
            id: `trk_paste_${Date.now()}`,
            name: 'Guitar',
            instrument: 'Distortion Guitar',
            tuningName: 'Standard E',
            tuningNotes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
            volume: 0.9,
            isMuted: false,
            isSolo: false,
            measures,
          },
        ],
      };
      onImportScore(newScore);
      onClose();
    }
  };

  // Export handlers
  const handleDownloadFile = (content: string, filename: string, mime: string) => {
    const blob = new Blob([content], { type: mime });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleExportAscii = () => {
    const ascii = exportSongToAscii(score);
    handleDownloadFile(ascii, `${score.title.replace(/\s+/g, '_')}_tab.txt`, 'text/plain');
  };

  const handleExportAlphaTex = () => {
    const tex = convertSongScoreToAlphaTex(score, false);
    handleDownloadFile(tex, `${score.title.replace(/\s+/g, '_')}.tex`, 'text/plain');
  };

  const handleExportJson = () => {
    const json = JSON.stringify(score, null, 2);
    handleDownloadFile(json, `${score.title.replace(/\s+/g, '_')}_tuxguitar.json`, 'application/json');
  };

  const handleCopyAscii = () => {
    const ascii = exportSongToAscii(score);
    navigator.clipboard.writeText(ascii);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-[#0F1420] border border-[#27344D] rounded-3xl max-w-xl w-full p-6 space-y-5 shadow-2xl animate-fadeIn">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-[#1E293B]">
          <div className="flex items-center space-x-2">
            <h3 className="text-base font-black text-white">TuxGuitar Project I/O</h3>
            <span className="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-300 font-mono text-[10px] font-bold">
              GP / AlphaTex / ASCII
            </span>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-xl text-zinc-400 hover:text-white hover:bg-[#1E273A] transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab switchers */}
        <div className="flex items-center bg-[#090D15] p-1 rounded-xl border border-[#1E2638]">
          <button
            onClick={() => setActiveTab('import')}
            className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-colors flex items-center justify-center space-x-1.5 ${
              activeTab === 'import'
                ? 'bg-amber-500 text-zinc-950 shadow-md'
                : 'text-zinc-400 hover:text-white'
            }`}
          >
            <Upload className="w-3.5 h-3.5" />
            <span>Import Tab / Project</span>
          </button>
          <button
            onClick={() => setActiveTab('export')}
            className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-colors flex items-center justify-center space-x-1.5 ${
              activeTab === 'export'
                ? 'bg-amber-500 text-zinc-950 shadow-md'
                : 'text-zinc-400 hover:text-white'
            }`}
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export & Save</span>
          </button>
        </div>

        {statusMessage && (
          <div className="p-3 rounded-xl bg-teal-500/10 border border-teal-500/30 text-teal-300 text-xs font-bold flex items-center space-x-2">
            <FileCheck className="w-4 h-4 text-teal-400" />
            <span>{statusMessage}</span>
          </div>
        )}

        {/* IMPORT TAB */}
        {activeTab === 'import' && (
          <div className="space-y-4">
            {/* File Upload Box */}
            <div className="border-2 border-dashed border-[#27344D] hover:border-amber-400/60 rounded-2xl p-5 text-center transition-colors bg-[#0A0E18]">
              <Upload className="w-8 h-8 text-amber-400 mx-auto mb-2" />
              <p className="text-xs font-bold text-white mb-1">
                Drop your tablature or project file here
              </p>
              <p className="text-[11px] text-zinc-400 mb-3">
                Supports: TuxGuitar JSON (.json), ASCII Tab (.txt), AlphaTex (.tex)
              </p>
              <label className="inline-flex items-center px-4 py-2 rounded-xl bg-[#172030] hover:bg-[#202C42] border border-[#27344D] text-xs font-bold text-amber-300 cursor-pointer transition-colors shadow-sm">
                <span>Browse Files</span>
                <input
                  type="file"
                  accept=".json,.txt,.tex"
                  onChange={handleFileUpload}
                  className="hidden"
                />
              </label>
            </div>

            {/* Paste Text Tab */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-zinc-300">Or Paste Tab / Code:</label>
                <div className="flex items-center space-x-2 text-[11px]">
                  <button
                    onClick={() => setImportType('ascii')}
                    className={`px-2 py-0.5 rounded-md ${
                      importType === 'ascii' ? 'bg-amber-400/20 text-amber-300 font-bold' : 'text-zinc-500'
                    }`}
                  >
                    ASCII Tab
                  </button>
                  <button
                    onClick={() => setImportType('json')}
                    className={`px-2 py-0.5 rounded-md ${
                      importType === 'json' ? 'bg-amber-400/20 text-amber-300 font-bold' : 'text-zinc-500'
                    }`}
                  >
                    JSON
                  </button>
                </div>
              </div>

              <textarea
                rows={5}
                value={pasteContent}
                onChange={(e) => setPasteContent(e.target.value)}
                placeholder={
                  importType === 'ascii'
                    ? `e|----------------|\nB|----------------|\nG|--0---2---3-----|\nD|--0---2---3-----|\nA|----------------|\nE|----------------|`
                    : `Paste exported TuxGuitar JSON here...`
                }
                className="w-full p-3 rounded-xl bg-[#090D15] border border-[#222E42] text-amber-300 font-mono text-xs focus:border-amber-400 focus:outline-hidden"
              />

              <button
                onClick={handleParsePaste}
                disabled={!pasteContent.trim()}
                className="w-full py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 disabled:opacity-40 disabled:pointer-events-none text-zinc-950 font-bold text-xs flex items-center justify-center space-x-1.5 transition-colors cursor-pointer"
              >
                <Check className="w-3.5 h-3.5" />
                <span>Parse & Load into TuxGuitar</span>
              </button>
            </div>
          </div>
        )}

        {/* EXPORT TAB */}
        {activeTab === 'export' && (
          <div className="space-y-3">
            <p className="text-xs text-zinc-400">
              Download your current tab composition in standard guitar and audio formats:
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
              {/* AlphaTex Export */}
              <button
                onClick={handleExportAlphaTex}
                className="p-3.5 rounded-2xl bg-[#121824] hover:bg-[#1A2334] border border-[#222E42] text-left transition-colors flex items-center space-x-3 cursor-pointer group"
              >
                <div className="p-2.5 rounded-xl bg-amber-500/20 text-amber-400 group-hover:scale-105 transition-transform">
                  <FileCode className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-white">AlphaTex (.tex)</div>
                  <div className="text-[10px] text-zinc-400">Guitar Pro & alphaTab compatible</div>
                </div>
              </button>

              {/* ASCII Tab Export */}
              <button
                onClick={handleExportAscii}
                className="p-3.5 rounded-2xl bg-[#121824] hover:bg-[#1A2334] border border-[#222E42] text-left transition-colors flex items-center space-x-3 cursor-pointer group"
              >
                <div className="p-2.5 rounded-xl bg-teal-500/20 text-teal-400 group-hover:scale-105 transition-transform">
                  <FileText className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-white">ASCII Tab (.txt)</div>
                  <div className="text-[10px] text-zinc-400">Standard printable text tab</div>
                </div>
              </button>

              {/* TuxGuitar JSON Project */}
              <button
                onClick={handleExportJson}
                className="p-3.5 rounded-2xl bg-[#121824] hover:bg-[#1A2334] border border-[#222E42] text-left transition-colors flex items-center space-x-3 cursor-pointer group"
              >
                <div className="p-2.5 rounded-xl bg-indigo-500/20 text-indigo-400 group-hover:scale-105 transition-transform">
                  <FileSpreadsheet className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-white">TuxGuitar Project (.json)</div>
                  <div className="text-[10px] text-zinc-400">Full multi-track backup with effects</div>
                </div>
              </button>

              {/* Copy ASCII to Clipboard */}
              <button
                onClick={handleCopyAscii}
                className="p-3.5 rounded-2xl bg-[#121824] hover:bg-[#1A2334] border border-[#222E42] text-left transition-colors flex items-center space-x-3 cursor-pointer group"
              >
                <div className="p-2.5 rounded-xl bg-emerald-500/20 text-emerald-400 group-hover:scale-105 transition-transform">
                  {copied ? <Check className="w-5 h-5" /> : <Copy className="w-5 h-5" />}
                </div>
                <div>
                  <div className="text-xs font-bold text-white">
                    {copied ? 'Copied to Clipboard!' : 'Copy ASCII Tab'}
                  </div>
                  <div className="text-[10px] text-zinc-400">Quick share to chat or notes</div>
                </div>
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
