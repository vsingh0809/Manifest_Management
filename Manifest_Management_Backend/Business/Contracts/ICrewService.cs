using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;
using Microsoft.AspNetCore.Mvc;

namespace ManifestPortal1.Business.Contracts
{
    public interface ICrewService
    {
        IEnumerable<Crewoperator> GetAllOperators();
        Crewoperator GetOperatorById(int id);
        Crewoperator GetOperatorByEmail(string email);
        Crewoperator UpdateOperator(int id, UpdateCrewoperatorDTO newOperator);

        Task<Crewoperator> RegisterCrewOperator(AddCrewoperatorDTO addOperator);
        Crewoperator UpdateEmail(int id, string newEmail);
        Crewoperator UpdatePassword(int id, string newPassword);
        Crewoperator UpdateImage(int id, byte[] newImage);

        Task<string> LoginCrewoperatorAsync(LoginDTO loginDto);

        bool VerifyOldPassword(string oldPassword, int id);
        Task<ServiceResponseDTO> UpdateCrewImageAsync(int crewId, UpdateCrewImageRequestDTO request);
    }
}
