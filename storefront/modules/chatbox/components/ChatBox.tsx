import React, { useState, useEffect, useRef } from 'react';
import Markdown from 'markdown-to-jsx';

import { chat } from '../services/ChatService';

export interface MessageInfo {
  sender: string;
  text: string;
  avatarBackgroundUser: string;
  avatarBackgroundBot: string;
}

export interface Colors {
  cardBackground: string;
  headerBackground: string;
  headerText: string;
  chatContainerBackground: string;
  userMessageBackground: string;
  botMessageBackground: string;
  userMessageTextColor: string;
  botMessageTextColor: string;
  avatarBackgroundBot: string;
  avatarBackgroundUser: string;
  inputBackground: string;
  inputTextColor: string;
  buttonBackground: string;
}

// Message component
interface MessageProps {
  message: MessageInfo;
  colors: Colors;
}

const Message = ({ message, colors }: MessageProps) => (
  <div
    className={`d-flex mb-2 ${message.sender === 'user' ? 'flex-row-reverse' : 'flex-row'}`}
    style={{ marginBottom: '15px', animation: 'messageFadeIn 0.5s forwards', alignItems: 'center' }}
  >
    <div
      className={`d-flex align-items-center justify-content-center ${
        message.sender === 'user' ? 'ms-1' : 'me-1'
      }`}
      style={{
        backgroundColor:
          message.sender === 'user' ? colors.avatarBackgroundUser : colors.avatarBackgroundBot,
        width: '38px',
        height: '38px',
        borderRadius: '50%',
        color: '#fff',
        fontWeight: 'bold',
        fontSize: '1.1rem',
        animation: 'avatarFadeIn 0.6s ease-out',
      }}
    >
      {message.sender === 'user' ? 'U' : 'Y'}
    </div>
    <div
      className="p-3"
      style={{
        maxWidth: '75%',
        borderRadius: message.sender === 'user' ? '20px 20px 0px 20px' : '20px 20px 20px 0px',
        backgroundColor:
          message.sender === 'user' ? colors.userMessageBackground : colors.botMessageBackground,
        color: message.sender === 'user' ? colors.userMessageTextColor : colors.botMessageTextColor,
        fontSize: '1.1rem',
        lineHeight: '1.8',
        padding: '12px 18px',
        wordBreak: 'break-word',
        animation: 'messageBubbleFadeIn 0.5s ease-out',
        fontFamily: 'Roboto, sans-serif',
      }}
    >
      <Markdown>{message.text}</Markdown>
    </div>
  </div>
);

// ChatBox component
interface ChatBoxProps {
  colors: Colors; // Ensure this is correctly typed
}

const ChatBox = ({ colors }: ChatBoxProps) => {
  const [messages, setMessages] = useState<MessageInfo[]>([
    {
      text: 'Hi there! How can I assist you today 🙂',
      sender: 'bot',
      avatarBackgroundUser: '',
      avatarBackgroundBot: '',
    },
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  // Send message handler
  const handleSendMessage = async () => {
    if (input.trim()) {
      setMessages((prev) => [
        ...prev,
        { text: input, sender: 'user', avatarBackgroundUser: '', avatarBackgroundBot: '' },
      ]);
      setInput('');
      setIsTyping(true);

      try {
        const response = await chat(input);
        setMessages((prev) => [
          ...prev,
          { text: response, sender: 'bot', avatarBackgroundUser: '', avatarBackgroundBot: '' },
        ]);
      } catch (error) {
        console.error('Error fetching data:', error);
        setMessages((prev) => [
          ...prev,
          {
            text: 'Sorry, something went wrong :(',
            sender: 'bot',
            avatarBackgroundUser: '',
            avatarBackgroundBot: '',
          },
        ]);
      } finally {
        setIsTyping(false);
      }
    }
  };

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  return (
    <div>
      <button
        className="btn"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Open/Close chat"
        style={{
          position: 'fixed',
          bottom: '20px',
          right: '20px',
          borderRadius: '50%',
          width: '60px',
          height: '60px',
          fontSize: '1.8rem',
          backgroundColor: '#000',
          color: '#fff',
          border: 'none',
          display: isOpen ? 'none' : 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          boxShadow: '0px 5px 25px rgba(0, 0, 0, 0.15)',
          transition: 'all 0.3s ease',
        }}
      >
        🗨️
      </button>

      {isOpen && (
        <div
          style={{
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            width: '560px',
            borderRadius: '35px 35px 0px 35px',
            backgroundColor: colors.cardBackground,
            zIndex: 1000,
            boxShadow: '0px 8px 16px rgba(0, 0, 0, 0.2), 0px 12px 32px rgba(0, 0, 0, 0.15)',
            overflow: 'hidden',
            transition: 'all 0.3s ease',
          }}
        >
          <div
            className="card-header d-flex align-items-center justify-content-between"
            style={{
              backgroundColor: colors.headerBackground,
              color: colors.headerText,
              height: '70px',
              borderBottom: '1px solid #eee',
              fontWeight: 'bold',
              fontSize: '1.7rem',
              fontFamily: 'Roboto, sans-serif',
            }}
          >
            <span style={{ paddingLeft: '10px' }}>Yasai</span>
            <button
              onClick={() => setIsOpen(false)}
              className="btn-close btn-close-white"
              aria-label="Close chat"
            ></button>
          </div>

          <div
            className="card-body"
            style={{ padding: '0.8rem', backgroundColor: colors.chatContainerBackground }}
          >
            <div
              style={{
                height: '350px',
                overflowY: 'auto',
                padding: '12px',
                borderRadius: '10px',
                backgroundColor: colors.chatContainerBackground,
              }}
            >
              {messages.map((msg, index) => (
                <Message key={index} message={msg} colors={colors} />
              ))}
              <div ref={messagesEndRef} />
            </div>

            {isTyping && (
              <div
                className="mt-2 text-muted"
                style={{
                  fontStyle: 'italic',
                  fontSize: '0.85rem',
                  animation: 'typingBounce 1.5s infinite',
                }}
              >
                Yas Agent is typing...
              </div>
            )}

            <div className="input-group mt-3" style={{ paddingTop: '10px' }}>
              <input
                type="text"
                className="form-control"
                placeholder="Type a message..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleSendMessage();
                }}
                aria-label="Message input"
                style={{
                  borderTopLeftRadius: '20px',
                  borderBottomLeftRadius: '20px',
                  borderRight: 'none',
                  outline: 'none',
                  fontSize: '1.1rem',
                  padding: '12px 18px',
                  backgroundColor: colors.inputBackground,
                  color: colors.inputTextColor,
                  transition: 'all 0.2s ease',
                  fontFamily: 'Roboto, sans-serif',
                }}
              />
              <button
                className="btn"
                onClick={handleSendMessage}
                aria-label="Send message"
                style={{
                  backgroundColor: colors.buttonBackground,
                  color: '#fff',
                  borderTopRightRadius: '20px',
                  borderBottomRightRadius: '20px',
                  padding: '12px 20px',
                  border: 'none',
                  boxShadow: 'none',
                  transition: 'all 0.3s ease',
                  fontFamily: 'Roboto, sans-serif',
                }}
              >
                Send
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatBox;
