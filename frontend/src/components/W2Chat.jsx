import { useState, useRef, useEffect } from 'react';
import { Box, TextField, Button, Paper, Typography, Avatar, IconButton } from '@mui/material';
import { Send, SmartToy, Person, Description, ContentCopy } from '@mui/icons-material';
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export default function W2Chat({ employeeName, onSummaryGenerated }) {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([
    {
      role: 'assistant',
      content: "Hello! I'm your tax advisor. I've reviewed your W-2 forms. To help you better, I'd like to ask a few questions about your tax situation. Let's start: What is your current marital status?"
    }
  ]);
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (!message.trim() || loading) return;

    const userMessage = message.trim();
    setMessage('');
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setLoading(true);

    try {
      const response = await axios.post(`${API_BASE}/analyze`, {
        employeeName,
        question: userMessage
      });
      setMessages(prev => [...prev, { role: 'assistant', content: response.data.answer }]);
    } catch (error) {
      console.error('Chat failed:', error);
      setMessages(prev => [...prev, { role: 'assistant', content: 'Sorry, there was an error processing your message.' }]);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateSummary = async () => {
    const userMessagesCount = messages.filter(m => m.role === 'user').length;
    if (userMessagesCount === 0) {
      alert('Please answer the tax advisor questions before generating a summary.');
      return;
    }

    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE}/summary/${encodeURIComponent(employeeName)}`);
      if (response.data.summary.startsWith('ERROR:')) {
        alert(response.data.summary.replace('ERROR: ', ''));
      } else {
        setMessages(prev => [...prev, { role: 'assistant', content: response.data.summary, isSummary: true }]);
        if (onSummaryGenerated) onSummaryGenerated();
      }
    } catch (error) {
      console.error('Summary generation failed:', error);
      setMessages(prev => [...prev, { role: 'assistant', content: 'Sorry, there was an error generating the summary.' }]);
    } finally {
      setLoading(false);
    }
  };

  const handleCopyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(() => {
      alert('Summary copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy:', err);
    });
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
          <SmartToy /> Tax Advisor Chat
        </Typography>
        <Button
          variant="contained"
          startIcon={<Description />}
          onClick={handleGenerateSummary}
          disabled={loading}
          sx={{ textTransform: 'none' }}
        >
          Generate Summary
        </Button>
      </Box>

      <Paper
        elevation={0}
        sx={{
          height: 500,
          overflowY: 'auto',
          p: 2,
          mb: 2,
          bgcolor: '#f8fafc',
          border: '1px solid #e2e8f0',
          '&::-webkit-scrollbar': { width: '8px' },
          '&::-webkit-scrollbar-thumb': { bgcolor: '#cbd5e1', borderRadius: '4px' }
        }}
      >
        {messages.map((msg, idx) => (
          <Box
            key={idx}
            sx={{
              display: 'flex',
              gap: 1.5,
              mb: 2,
              flexDirection: msg.role === 'user' ? 'row-reverse' : 'row',
              animation: 'fadeIn 0.3s ease'
            }}
          >
            <Avatar
              sx={{
                width: 36,
                height: 36,
                bgcolor: msg.role === 'user' ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : 'white',
                border: msg.role === 'assistant' ? '2px solid #e2e8f0' : 'none',
                background: msg.role === 'user' ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : 'white'
              }}
            >
              {msg.role === 'user' ? <Person /> : <SmartToy sx={{ color: '#667eea' }} />}
            </Avatar>
            <Paper
              elevation={0}
              sx={{
                p: 1.5,
                maxWidth: '75%',
                bgcolor: msg.role === 'user' ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : 'white',
                background: msg.role === 'user' ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : 'white',
                color: msg.role === 'user' ? 'white' : '#334155',
                boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
                position: 'relative'
              }}
            >
              <Typography variant="body2" sx={{ lineHeight: 1.6, whiteSpace: 'pre-wrap' }}>
                {msg.content}
              </Typography>
              {msg.isSummary && (
                <IconButton
                  size="small"
                  onClick={() => handleCopyToClipboard(msg.content)}
                  sx={{
                    position: 'absolute',
                    top: 8,
                    right: 8,
                    bgcolor: '#f1f5f9',
                    '&:hover': { bgcolor: '#e2e8f0' }
                  }}
                >
                  <ContentCopy sx={{ fontSize: 16 }} />
                </IconButton>
              )}
            </Paper>
          </Box>
        ))}
        {loading && (
          <Box sx={{ display: 'flex', gap: 1.5, mb: 2 }}>
            <Avatar sx={{ width: 36, height: 36, bgcolor: 'white', border: '2px solid #e2e8f0' }}>
              <SmartToy sx={{ color: '#667eea' }} />
            </Avatar>
            <Paper elevation={0} sx={{ p: 1.5, bgcolor: 'white' }}>
              <Typography variant="body2" color="text.secondary">Thinking...</Typography>
            </Paper>
          </Box>
        )}
        <div ref={messagesEndRef} />
      </Paper>

      <Box sx={{ display: 'flex', gap: 1 }}>
        <TextField
          fullWidth
          multiline
          maxRows={3}
          placeholder="Type your message here..."
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
          disabled={loading}
          sx={{ bgcolor: 'white' }}
        />
        <IconButton
          color="primary"
          onClick={handleSend}
          disabled={loading || !message.trim()}
          sx={{
            bgcolor: 'primary.main',
            color: 'white',
            '&:hover': { bgcolor: 'primary.dark' },
            '&:disabled': { bgcolor: '#e2e8f0', color: '#94a3b8' }
          }}
        >
          <Send />
        </IconButton>
      </Box>
    </Box>
  );
}
