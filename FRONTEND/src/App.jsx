import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import { AppProvider } from "./context/AppContext";
import AppLayout from "./layouts/AppLayout";
import Dashboard from "./pages/Dashboard";
import Applications from "./pages/Applications";
import ApplicationDetails from "./pages/ApplicationDetails";
import Interviews from "./pages/Interviews";
import FollowUps from "./pages/FollowUps";
import Settings from "./pages/Settings";
import About from "./pages/About";
import Login from "./pages/Login";
import Register from "./pages/Register";

function Protected({ children }) {
  return localStorage.getItem("jobtrack_token") ? (
    children
  ) : (
    <Navigate to="/login" replace />
  );
}
function Public({ children }) {
  return localStorage.getItem("jobtrack_token") ? (
    <Navigate to="/" replace />
  ) : (
    children
  );
}

export default function App() {
  return (
    <AppProvider>
      <Routes>
        <Route
          path="/login"
          element={
            <Public>
              <Login />
            </Public>
          }
        />
        <Route
          path="/register"
          element={
            <Public>
              <Register />
            </Public>
          }
        />
        <Route
          element={
            <Protected>
              <AppLayout />
            </Protected>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="applications" element={<Applications />} />
          <Route path="applications/:id" element={<ApplicationDetails />} />
          <Route path="interviews" element={<Interviews />} />
          <Route path="follow-ups" element={<FollowUps />} />
          <Route path="settings" element={<Settings />} />
          <Route path="about" element={<About />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AppProvider>
  );
}
